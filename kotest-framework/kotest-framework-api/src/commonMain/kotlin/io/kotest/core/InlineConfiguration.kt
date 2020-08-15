package io.kotest.core

import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseConfig
import kotlin.js.JsName

/**
 * Defines variables and functions which can be used to set configuration options on tests.
 * This is an alternative style to using [SpecFunctionConfiguration].
 */
interface InlineConfiguration {

   /**
    * Config applied to each test case if not overridden per test case.
    * If left null, then defaults to the project default.
    */
   var defaultTestConfig: TestCaseConfig?

   @JsName("isolation_mode_var")
   var isolationMode: IsolationMode?

   /**
    * Sets an assertion mode which is applied to every test.
    */
   var assertions: AssertionMode?

   /**
    * Sets the number of root test cases that can be executed concurrently in this spec.
    * On the JVM this will result in multiple threads being used.
    * On other platforms this setting will have no effect.
    * Defaults to 1.
    */
   @JsName("threadsJs")
   var threads: Int?

   /**
    * Adds [Tag]s to this spec or factory, which will be applied to each test case.
    *
    * When applied in a factory, only tests generated from that factory will have the tags applied.
    * When applied to a spec, all tests will have the tags applied.
    */
   fun tags(vararg tags: Tag)

   /**
    * Returns tags that should be added to each test case.
    */
   fun inlinetags(): Set<Tag>

   /**
    * Register a single [TestListener] of type T return that listener.
    */
   fun <T : TestListener> listener(listener: T): T

   /**
    * Register multiple [TestListener]s.
    */
   fun listeners(vararg listener: TestListener)

   /**
    * Register a single [TestCaseExtension] of type T return that extension.
    */
   fun <T : TestCaseExtension> extension(extension: T): T

   /**
    * Register multiple [TestCaseExtension]s.
    */
   fun extensions(vararg extensions: TestCaseExtension)
}
