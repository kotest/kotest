package io.kotest.core.spec

import io.kotest.core.Tag
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestCaseOrder

/**
 * Defines functions which can be overriden to set configuration options on tests.
 * This is an alternative style to using [InlineConfiguration].
 */
interface FunctionConfiguration {

   fun defaultTestCaseConfig(): TestCaseConfig? = null

   /**
    * Override this function to register extensions
    * which will be invoked during execution of this spec.
    *
    * If you wish to register an extension across the project
    * then use [AbstractProjectConfig.extensions].
    */
   fun extensions(): List<Extension> = listOf()

   /**
    * Override this function to register instances of
    * [TestListener] which will be notified of events during
    * execution of this spec.
    */
   fun listeners(): List<TestListener> = emptyList()

   /**
    * Sets the order of root [TestCase]s in this spec.
    * If this function returns a null value, then the global default will be used.
    */
   fun testCaseOrder(): TestCaseOrder? = null

   /**
    * Any tags added here will be in applied to all [TestCase]s defined
    * in this [AbstractSpec] in addition to any defined on the individual
    * tests themselves.
    *
    * Note: The spec instance will still need to be instantiated to retrieve these tags.
    * If you want to exclude a Spec without an instance being created, use @Tags
    * on the Spec class.
    */
   fun tags(): Set<Tag> = emptySet()

   fun isolationMode(): IsolationMode? = null

   fun assertionMode(): AssertionMode? = null

   fun threads(): Int? = null
}
