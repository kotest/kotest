package io.kotest.core.extensions

import io.kotest.core.config.configuration
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase

/**
 * Returns the runtime resolved [TestCaseExtension]s applicable for this [TestCase].
 * Those are extensions from the test case's [TestCaseConfig] and those applicable to
 * the spec instance.
 */
fun TestCase.resolvedTestCaseExtensions(): List<TestCaseExtension> {
   return config.extensions + spec.resolvedExtensions().filterIsInstance<TestCaseExtension>()
}

fun Spec.resolvedSpecExtensions(): List<SpecExtension> = resolvedExtensions().filterIsInstance<SpecExtension>()

/**
 * Returns all [Extension]s applicable to this [Spec]. This includes extensions via the
 * function override, those registered explicitly in the spec, and project wide extensions
 * from configuration.
 */
fun Spec.resolvedExtensions(): List<Extension> {
   return this.extensions() + this.registeredExtensions() + configuration.extensions()
}
