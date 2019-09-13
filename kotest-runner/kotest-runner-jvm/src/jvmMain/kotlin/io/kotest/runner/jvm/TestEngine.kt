package io.kotest.runner.jvm

import arrow.core.Try
import io.kotest.DoNotParallelize
import io.kotest.Project
import io.kotest.Spec
import io.kotest.Tag
import io.kotest.core.TestCaseFilter
import io.kotest.extensions.SpecifiedTagsTagExtension
import io.kotest.runner.jvm.internal.NamedThreadFactory
import io.kotest.runner.jvm.spec.SpecExecutor
import org.slf4j.LoggerFactory
import java.util.Collections.emptyList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class TestEngine(val classes: List<KClass<out Spec>>,
                 filters: List<TestCaseFilter>,
                 val parallelism: Int,
                 includedTags: Set<Tag>,
                 excludedTags: Set<Tag>,
                 val listener: TestEngineListener) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  // the scheduler executor is used for notifications on when a test case timeout has been reached
  private val scheduler = Executors.newSingleThreadScheduledExecutor()

  private val specExecutor = SpecExecutor(listener, scheduler)

  init {
    Project.registerTestCaseFilter(filters)
    if (includedTags.isNotEmpty() || excludedTags.isNotEmpty())
      Project.registerExtension(SpecifiedTagsTagExtension(includedTags, excludedTags))
  }

  private fun afterAll() = Try { Project.afterAll() }

  private fun start() = Try {
    listener.engineStarted(classes)
    Project.beforeAll()
  }

  private fun submitAll() = Try {
    logger.trace("Submitting ${classes.size} specs")

    // the classes are ordered using an instance of SpecExecutionOrder
    val specs = Project.specExecutionOrder().sort(classes)

    // if parallelize is enabled, then we must order the specs into two sets, depending on if they
    // are thread safe or not.
    val (single, parallel) = if (parallelism == 1)
      specs to emptyList()
    else
      specs.partition { it.isDoNotParallelize() }

    submitBatch(parallel, parallelism)
    submitBatch(single, 1)
  }

  private fun submitBatch(specs: List<KClass<out Spec>>, parallelism: Int) {
    val executor = Executors.newFixedThreadPool(parallelism,
      NamedThreadFactory("kotest-engine-%d"))
    specs.forEach { submitSpec(it, executor) }
    executor.shutdown()

    logger.trace("Waiting for spec execution to terminate")
    val error = try {
      executor.awaitTermination(1, TimeUnit.DAYS)
      null
    } catch (t: InterruptedException) {
      t
    }

    if (error != null)
      throw error
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

  private fun submitSpec(klass: KClass<out Spec>, executor: ExecutorService) {
    executor.submit {
      createSpec(klass).fold(
          { t ->
            listener.specInitialisationFailed(klass, t)
            executor.shutdownNow()
          },
          { spec ->
            specExecutor.execute(spec).onFailure { t ->
              // todo move this to a new listener method like specFailed(klass, t)
              listener.specInitialisationFailed(klass, t)
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
}

fun KClass<*>.isDoNotParallelize(): Boolean = findAnnotation<DoNotParallelize>() != null
