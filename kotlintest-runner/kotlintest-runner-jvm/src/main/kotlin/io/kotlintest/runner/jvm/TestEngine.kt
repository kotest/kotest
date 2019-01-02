package io.kotlintest.runner.jvm

import arrow.core.Try
import io.kotlintest.Description
import io.kotlintest.IsolationMode
import io.kotlintest.Project
import io.kotlintest.Spec
import io.kotlintest.runner.jvm.internal.NamedThreadFactory
import io.kotlintest.runner.jvm.spec.InstancePerLeafSpecRunner
import io.kotlintest.runner.jvm.spec.InstancePerTestCaseSpecRunner
import io.kotlintest.runner.jvm.spec.SingleInstanceSpecRunner
import io.kotlintest.runner.jvm.spec.SpecRunner
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

class TestEngine(val classes: List<KClass<out Spec>>,
                 parallelism: Int,
                 val listener: TestEngineListener) {

  private val logger = LoggerFactory.getLogger(this.javaClass)
  private val error = AtomicReference<Throwable?>(null)

  // the main executor is used to parallelize the execution of specs
  // inside a spec, tests themselves are executed as coroutines
  private val executor = Executors.newFixedThreadPool(parallelism, NamedThreadFactory("kotlintest-engine-%d"))

  private val scheduler = Executors.newSingleThreadScheduledExecutor()

  private val listenerExecutors = ConcurrentLinkedQueue<ExecutorService>()

  private fun afterAll() = Try {
    Project.afterAll()
  }

  private fun start() = Try {
    listener.engineStarted(classes)
    Project.beforeAll()
  }

  private fun submitAll() = Try {
    logger.debug("Submitting ${classes.size} specs")

    // the classes are ordered using an instance of SpecExecutionOrder before
    // being submitted in the order returned
    Project.specExecutionOrder().sort(classes).forEach { submitSpec(it) }
    executor.shutdown()

    logger.debug("Waiting for spec execution to terminate")
    try {
      executor.awaitTermination(1, TimeUnit.DAYS)
    } catch (t: InterruptedException) {
      error.compareAndSet(null, t)
    }

    // the executor may have terminated early because it was shutdown immediately
    // by an error in a submission. This will be reflected in the error reference
    // being set to a non null value
    val t = error.get()
    if (t != null)
      throw t
  }

  private fun end(t: Throwable?) = Try {
    if (t != null) {
      logger.error("Error during test engine run", t)
      t.printStackTrace()
    }
    listener.engineFinished(t)
  }

  fun execute() {
    start().flatMap { submitAll() }.fold(
        {
          afterAll()
          end(it)
        },
        {
          afterAll().fold(
              { t -> end(t) },
              { end(null) }
          )
        }
    )
  }

  private fun submitSpec(klass: KClass<out Spec>) {
    executor.submit {
      createSpec(klass).fold(
          // if there is an error creating the spec then we
          // will add a placeholder spec so we can see the error in intellij/gradle
          // otherwise it won't appear
          { t ->
            val desc = Description.root(klass.jvmName)
            listener.prepareSpec(desc, klass)
            listener.completeSpec(desc, klass, t)
            error.compareAndSet(null, t)
            executor.shutdownNow()
          },
          { spec ->
            executeSpec(spec).onf { t ->
              error.compareAndSet(null, t)
              executor.shutdownNow()
            }
          }
      )
    }
  }

  private fun createSpec(klass: KClass<out Spec>) =
      instantiateSpec(klass).flatMap {
        Try {
          listener.specCreated(it)
          it
        }
      }

  private fun executeSpec(spec: Spec) = Try {
    listener.prepareSpec(spec.description(), spec::class)
    val listenerExecutor = listenerExecutors.poll() ?: Executors.newSingleThreadExecutor()
    Try {
      spec.beforeSpecStarted(spec.description(), spec)
      val runner = runner(spec, listenerExecutor, scheduler)
      runner.execute(spec)
      listenerExecutors.add(listenerExecutor)
      spec.afterSpecCompleted(spec.description(), spec)
    }.fold(
        {
          logger.debug("Completing spec ${spec.description()} with error $it")
          listener.completeSpec(spec.description(), spec.javaClass.kotlin, it)
        },
        {
          logger.debug("Completing spec ${spec.description()} with success")
          listener.completeSpec(spec.description(), spec.javaClass.kotlin, null)
        }
    )
    spec.closeResources()
  }

  // each runner must get a single-threaded executor, which is used to invoke
  // listeners/extensions and the test itself when testcase.config.threads=1
  // otherwise, the listeners and the tests can be run on seperate threads,
  // which is undesirable in some situations, see
  // https://github.com/kotlintest/kotlintest/issues/447
  private fun runner(spec: Spec, listenerExecutor: ExecutorService, scheduler: ScheduledExecutorService): SpecRunner {
    return when (spec.isolationMode()) {
      IsolationMode.SingleInstance -> SingleInstanceSpecRunner(listener, listenerExecutor, scheduler)
      IsolationMode.InstancePerTest -> InstancePerTestCaseSpecRunner(listener, listenerExecutor, scheduler)
      IsolationMode.InstancePerLeaf -> InstancePerLeafSpecRunner(listener, listenerExecutor, scheduler)
      null -> when {
        spec.isInstancePerTest() -> InstancePerTestCaseSpecRunner(listener, listenerExecutor, scheduler)
        else -> SingleInstanceSpecRunner(listener, listenerExecutor, scheduler)
      }
    }
  }
}