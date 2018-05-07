package io.kotlintest.runner.jvm

import arrow.core.Try
import arrow.core.transform
import io.kotlintest.Project
import io.kotlintest.Spec
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class TestEngine(val classes: List<KClass<out Spec>>, val listener: TestEngineListener) {

  private val logger = LoggerFactory.getLogger(this.javaClass)

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

    fun submit() = Try {
      logger.error("Submitting ${classes.size} specs")
      classes.forEach { submitSpec(it) }
    }

    fun end(t: Throwable?) = Try { listener.engineFinished(t) }

    val t = start().flatMap {
      beforeAll().flatMap {
        submit()
      }
    }

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

  // inside the init method of a spec) then we fail fast (terminate all specs)
//        specsExecutor.shutdownNow()
//        specsExecutor.awaitTermination(1, TimeUnit.DAYS)
//        logger.debug("Spec thread pool has terminated")

  private fun specExecutor(spec: Spec): SpecExecutor =
      when {
        spec.isInstancePerTest() -> InstancePerTestSpecExecutor(listener)
        else -> SingleInstanceSpecExecutor(listener)
      }

  fun submitSpec(klass: KClass<out Spec>) {

    // we need to instantiate the spec outside of the executor
    // so any error will be caught and shutdown the executor
    val spec = createSpecInstance(klass)
    logger.debug("Spec instance created [$klass=$spec]")

    val executor = specExecutor(spec)
    logger.debug("Created spec executor [$executor")

    fun completeSpec(t: Throwable?) {
      try {
        listener.completeSpec(spec, t)
      } catch (t: Throwable) {
        t.printStackTrace()
        logger.error("Error when completing spec", t)
      }
    }

    try {
      listener.prepareSpec(spec)
      executor.execute(spec)
      completeSpec(null)
    } catch (t: Throwable) {
      t.printStackTrace()
      logger.error("Error when executing spec", t)
      completeSpec(t)
    }
  }
}