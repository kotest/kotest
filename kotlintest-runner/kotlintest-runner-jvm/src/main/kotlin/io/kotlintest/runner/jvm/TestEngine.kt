package io.kotlintest.runner.jvm

import arrow.core.Try
import arrow.core.transform
import io.kotlintest.Project
import io.kotlintest.Spec
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class TestEngine(val classes: List<KClass<out Spec>>, val listener: TestEngineListener) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

  private val executor = Project.parallelism().let {
    logger.info("Creating spec executor service with $it threads")
    Executors.newFixedThreadPool(it)
  }

  fun execute() {

    fun start() = Try { listener.engineStarted(classes) }

    fun beforeAll() = Try {
      logger.error("Executing before all")
      Project.beforeAll()
    }

    fun afterAll() = Try {
      logger.error("Executing after all")
      Project.afterAll()
    }

    fun submitAll() = Try {
      logger.error("Submitting ${classes.size} specs")
      classes.forEach { submitSpec(it) }
    }

    fun end(t: Throwable?) = Try { listener.engineFinished(t) }

    val t = start().flatMap {
      beforeAll().flatMap {
        submitAll()
      }
    }

    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.DAYS)
    logger.debug("Spec executor has terminated")

    val u = t.transform({
      afterAll()
    }, {
      afterAll()
      t
    })

    u.fold({
      end(it)
      throw it
    }, {
      end(null)
    })
  }

  fun submitSpec(klass: KClass<out Spec>) {

    // we need to instantiate the spec outside of the executor
    // so any error will be caught and shutdown the executor
    val spec = createSpecInstance(klass)
    logger.debug("Spec instance created [$klass=$spec]")

    executor.submit {

      val runner = runner(spec)
      logger.debug("Created spec runner [$runner")

      fun completeSpec(t: Throwable?) {
        try {
          listener.completeSpec(spec, t)
        } catch (t: Throwable) {
          t.printStackTrace()
          logger.error("Error when completing spec", t)
          executor.shutdownNow()
        }
      }

      try {
        listener.prepareSpec(spec)
        runner.execute(spec)
        completeSpec(null)
      } catch (t: Throwable) {
        t.printStackTrace()
        logger.error("Error when executing spec", t)
        completeSpec(t)
        executor.shutdownNow()
      }
    }
  }

  private fun runner(spec: Spec): SpecRunner =
      when {
        spec.isInstancePerTest() -> InstancePerTestSpecRunner(listener)
        else -> SharedInstanceSpecRunner(listener)
      }
}