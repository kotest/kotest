package io.kotlintest.runner.jvm

import io.kotlintest.Project
import io.kotlintest.Spec
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class TestEngine(val classes: List<KClass<out Spec>>, val listener: TestEngineListener) {

  fun execute() {
    try {

      listener.engineStarted(classes)

      // we execute each spec inside a thread pool so we can parallelise spec execution.
      val specsExecutor = Executors.newFixedThreadPool(Project.parallelism())

      try {
        Project.beforeAll()
        classes.forEach {

          // we need to instantiate the spec outside of the executor
          // so any error will be caught and shutdown the executor
          val spec = createSpecInstance(it)

          val executor = when {
            spec.isInstancePerTest() -> InstancePerTestSpecExecutor(listener)
            else -> SingleInstanceSpecExecutor(listener)
          }

          specsExecutor.submit {
            try {
              listener.prepareSpec(spec)
              executor.execute(spec)
              listener.completeSpec(spec, null)
            } catch (t: Throwable) {
              listener.completeSpec(spec, t)
            }
          }
        }

        specsExecutor.shutdown()
        specsExecutor.awaitTermination(1, TimeUnit.DAYS)

      } catch (t: Throwable) {
        // if we pick up an error in a spec that isn't caught as part of a test case (usually
        // inside the init method of a spec) then we fail fast (terminate all specs)
        specsExecutor.shutdownNow()
        specsExecutor.awaitTermination(1, TimeUnit.DAYS)

        // re-throwing so the outer block picks it up and notifies the listener
        throw t

      } finally {
        Project.afterAll()
      }

      listener.engineFinished(null)

    } catch (t: Throwable) {
      t.printStackTrace()
      listener.engineFinished(t)
    }
  }
}