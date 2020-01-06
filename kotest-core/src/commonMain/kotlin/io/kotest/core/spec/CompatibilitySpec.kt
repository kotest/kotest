package io.kotest.core.spec

import io.kotest.core.AssertionMode
import io.kotest.core.Tag
import io.kotest.core.TestCaseOrder
import io.kotest.extensions.SpecLevelExtension
import io.kotest.extensions.TestListener

/**
 * Contains functions which can be overriden to set config in the same way that KotlinTest 3.x allowed.
 * This style of configuration is deprecated and this class exists purely to ease the migration from
 * KotlinTest 3.x to Kotest 4.0.
 */
abstract class CompatibilitySpecConfiguration {

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
    * in this [SpecClass] in addition to any defined on the individual
    * tests themselves.
    */
   fun tags(): Set<Tag> = emptySet()

   fun assertionMode(): AssertionMode? = null
}
