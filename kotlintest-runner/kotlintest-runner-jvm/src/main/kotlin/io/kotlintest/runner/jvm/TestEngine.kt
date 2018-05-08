package io.kotlintest.runner.jvm

import arrow.core.Try
import io.kotlintest.Project
import io.kotlintest.Spec
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

class TestEngine(val classes: List<KClass<out Spec>>,
                 val parallelism: Int,
                 val listener: TestEngineListener) {

  private val logger = LoggerFactory.getLogger(this.javaClass)
  private val executor = Executors.newFixedThreadPool(parallelism)
  private val error = AtomicReference<Throwable?>(null)

  private fun afterAll() = Try {
    Project.afterAll()
  }

  private fun start() = Try {
    listener.engineStarted(classes)
    Project.beforeAll()
  }

  private fun submitAll() = Try {
    logger.debug("Submitting ${classes.size} specs")

    classes.forEach { submitSpec(it) }
    executor.shutdown()

    logger.debug("Waiting for spec execution service to terminate")
    executor.awaitTermination(1, TimeUnit.DAYS)

    // the executor may have terminated early because it was shutdown immediately
    // by an error in a submission. This will be reflected in the error reference
    // being set to non null

    error.get().let {
      when (it) {
        null -> Try.just(Unit)
        else -> Try.raise(it)
      }
    }
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
              { end(it) },
              { end(null) }
          )
        }
    )
  }

  private fun submitSpec(klass: KClass<out Spec>) {
    val onError = { t: Throwable ->
      executor.shutdownNow()
      error.set(t)
    }
    executor.submit {
      createSpec(klass).fold(onError, {
        executeSpec(it).onf(onError)
      })
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
    listener.prepareSpec(spec)
    Try {
      runner(spec).execute(spec)
    }.fold(
        { listener.completeSpec(spec, it) },
        { listener.completeSpec(spec, null) }
    )
  }

  private fun runner(spec: Spec): SpecRunner =
      when {
        spec.isInstancePerTest() -> InstancePerTestSpecRunner(listener)
        else -> SharedInstanceSpecRunner(listener)
      }
}