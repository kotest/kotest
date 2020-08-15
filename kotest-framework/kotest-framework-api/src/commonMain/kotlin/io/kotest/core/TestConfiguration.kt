package io.kotest.core

import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AutoCloseable
import io.kotest.core.test.AssertionMode
import io.kotest.core.test.TestCaseConfig
import kotlin.js.JsName

/**
 * A [TestConfiguration] is an abstract implementation of [InlineConfiguration] and [InlineCallbacks]
 * and forms the base implementation for [Spec] and [TestFactoryConfiguration]
 */
abstract class TestConfiguration : InlineCallbacks, InlineConfiguration, AutoClosing, FactorySupport {

   override var defaultTestConfig: TestCaseConfig? = null

   override var assertions: AssertionMode? = null

   @JsName("_tags")
   internal var tags: Set<Tag> = emptySet()

   @JsName("_listeners")
   internal var listeners = emptyList<TestListener>()

   @JsName("_extensions")
   internal var extensions = emptyList<Extension>()

   @JsName("_autoCloseables")
   internal var autoCloseables = emptyList<AutoCloseable>()

   override var threads: Int? = null

   override fun autocloseables(): List<Lazy<AutoCloseable>> = autocloseables().toList()

   override fun <T : TestListener> listener(listener: T): T {
      listeners(listener)
      return listener
   }

   override fun listeners(vararg listener: TestListener) {
      this.listeners = this.listeners + listener.toList()
   }

   override fun <T : TestCaseExtension> extension(extension: T): T {
      extensions(extension)
      return extension
   }

   override fun extensions(vararg extensions: TestCaseExtension) {
      this.extensions = this.extensions + extensions.toList()
   }

   override fun tags(vararg tags: Tag) {
      this.tags = this.tags + tags.toSet()
   }

   override fun <T : AutoCloseable> autoClose(closeable: T): T {
      autoCloseables = listOf(closeable) + autoCloseables
      return closeable
   }

   override fun <T : AutoCloseable> autoClose(closeable: Lazy<T>): Lazy<T> {
      autoClose(closeable.value)
      return closeable
   }
}
