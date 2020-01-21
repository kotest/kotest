package io.kotest.core.runtime

import io.kotest.core.config.Project
import io.kotest.core.config.dumpProjectConfig
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.Spec
import io.kotest.core.spec.resolvedExtensions
import io.kotest.core.spec.resolvedTestListeners
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.fp.Try

/**
 * Invokes the after project listeners.
 */
fun afterAll() = Try { Project.projectListeners().forEach { it.afterProject() } }

/**
 * Invokes the before project listeners, and prints project config using [dumpProjectConfig].
 */
fun beforeAll() = Try {
   Project.dumpProjectConfig()
   Project.projectListeners().forEach { it.beforeProject() }
}

suspend fun TestCase.invokeBeforeTest() {
   val listeners = spec.resolvedTestListeners() + Project.testListeners()
   listeners.forEach {
      it.beforeTest(this)
   }
}

suspend fun TestCase.invokeAfterTest(result: TestResult) {
   val listeners = spec.resolvedTestListeners() + Project.testListeners()
   listeners.forEach {
      it.afterTest(this, result)
   }
}

/**
 * Notifies the user listeners that a [Spec] is starting.
 * This will be invoked for every instance of a spec.
 */
suspend fun Spec.invokeBeforeSpec( ): Try<Spec> = Try {
   val listeners = resolvedTestListeners() + Project.testListeners()
   listeners.forEach {
      it.beforeSpec(this)
   }
   this
}

/**
 * Notifies the user listeners that a [Spec] has finished.
 * This will be invoked for every instance of a spec.
 */
suspend fun Spec.invokeAfterSpec( ): Try<Spec> = Try {
   val listeners = resolvedTestListeners() + Project.testListeners()
   listeners.forEach {
      it.afterSpec(this)
   }
   this
}

/**
 * Returns the runtime resolved [TestCaseExtension]s applicable for this [TestCase].
 * Those are extensions registered on the test case's own config, those registered
 * on the spec instance, and those registered at the project level.
 */
fun TestCase.extensions(): List<TestCaseExtension> {
   return config.extensions +
      spec.resolvedExtensions().filterIsInstance<TestCaseExtension>() +
      Project.testCaseExtensions()
}
