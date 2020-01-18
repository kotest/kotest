package io.kotest.core.spec

import io.kotest.core.*
import io.kotest.core.test.*
import io.kotest.core.extensions.SpecLevelExtension
import io.kotest.core.extensions.TestListener

/**
 * Contains methods which can be overriden to set config in the same way that KotlinTest 3.x allowed.
 * The preferred style is to call the DSL functions from within the tests but these methods are still
 * supported and will not be deprecated.
 */
interface CompatibilitySpecConfiguration {

   fun defaultTestCaseConfig(): TestCaseConfig? = null

   /**
    * Override this function to register extensions
    * which will be invoked during execution of this spec.
    *
    * If you wish to register an extension across the project
    * then use [AbstractProjectConfig.extensions].
    */
   fun extensions(): List<SpecLevelExtension> = listOf()

   /**
    * Override this function to register instances of
    * [TestListener] which will be notified of events during
    * execution of this spec.
    *
    * If you wish to register a listener that will be notified
    * for all specs, then use [AbstractProjectConfig.listeners].
    */
   fun listeners(): List<TestListener> = emptyList()

   /**
    * Sets the order of top level [TestCase]s in this spec.
    * If this function returns a null value, then the value set in
    * the [AbstractProjectConfig] will be used.
    */
   fun testCaseOrder(): TestCaseOrder? = null

   /**
    * Any tags added here will be in applied to all [TestCase]s defined
    * in this [SpecConfiguration] in addition to any defined on the individual
    * tests themselves.
    */
   fun tags(): Set<Tag> = emptySet()

   fun isolationMode(): IsolationMode? = null

   fun assertionMode(): AssertionMode? = null

   fun afterSpec(spec: SpecConfiguration) {}

   fun afterTest(testCase: TestCase, result: TestResult) {}

   fun beforeSpec(spec: SpecConfiguration) {}

   fun beforeTest(testCase: TestCase) {}
}
