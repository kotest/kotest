package io.kotest.core.spec

import io.kotest.common.reflection.bestName
import io.kotest.core.Tag
import io.kotest.core.factory.TestFactory
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.ContextAwareListener
import io.kotest.core.spec.style.scopes.RootScope
import kotlin.js.JsName

/**
 * Public API for creating a custom spec style.
 *
 * Note: There is no difference between this and the parent, but it is simply a better name and one
 * that is being chosen as part of the public API.
 */
@Suppress("DEPRECATION")
abstract class AbstractSpec : DslDrivenSpec()

/**
 * Base class for specs that allow for registration of tests via a DSL.
 */
@Deprecated("Was never intended as a public API. Use io.kotest.core.spec.AbstractSpec instead which is part of the public API. Deprecated in 6.2")
abstract class DslDrivenSpec : Spec(), RootScope {

   /**
    * Contains the [@KotestInternal]s that have been registered on this spec.
    */
   @Suppress("DEPRECATION")
   @JsName("tests_js")
   private var tests = emptyList<TestDefinition>()

   /**
    * Marks that this spec has been instantiated and all root tests have been registered.
    * After this point, no further root tests are allowed to be defined.
    */
   internal var sealed = false

   @Suppress("DEPRECATION")
   override fun tests(): List<TestDefinition> {
      return tests
   }

   /**
    * Register a [RootTest] with this spec.
    * This function may only be called before tests in the spec begin executing.
    * If this function is called after tests have started executing, an [InvalidDslException] will be thrown.
    */
   @Suppress("DEPRECATION")
   @Deprecated("Use add(TestDefinition). Deprecated since 6.2. Will be removed in 7.0")
   override fun add(test: RootTest) {
      if (sealed) throw InvalidDslException("Cannot add a root test after the spec has been instantiated: ${test.name.name}")
      tests = tests + TestDefinition(
         name = test.name,
         config = test.config,
         type = test.type,
         test = test.test,
         source = test.source,
         xmethod = test.xmethod,
         factoryId = test.factoryId,
      )
   }

   override fun add(test: TestDefinition) {
      if (sealed) throw InvalidDslException("Cannot add a root test after the spec has been instantiated: ${test.name.name}")
      @Suppress("DEPRECATION")
      tests = tests + test
   }

   override fun tags(vararg tags: Tag) {
      if (sealed) throw InvalidDslException("Cannot add a tag after the spec has been instantiated")
      super.tags(*tags)
   }

   /**
    * Include the tests and extensions from the given [TestFactory] in this spec.
    * Tests are added in order from where this function was invoked using configuration and
    * settings at the time the method was invoked.
    */
   fun include(factory: TestFactory) {
      factory.tests.forEach { add(it.copy(factoryId = factory.factoryId)) }
      factory.configuration.setParentConfiguration(this)
      extensions(factory.extensions)
   }

   /**
    * Includes the tests from the given [TestFactory] in this spec or factory, with the given
    * prefixed added to each of the test's names.
    */
   fun include(prefix: String, factory: TestFactory) {
      val renamed = factory.tests.map { test ->
         val name = test.name.copy(name = prefix + " " + test.name.name)
         test.copy(name = name)
      }
      include(factory.copy(tests = renamed))
   }

   /**
    * Registers a callback that will execute after all specs have completed.
    *
    * This is a convenience method for creating an [AfterProjectListener] and registering
    * it with project configuration.
    */
   fun afterProject(f: AfterProject) {
      afterProjectListeners.add(object : AfterProjectListener, ContextAwareListener {
         override suspend fun afterProject() {
            f()
         }

         override val context: String = this@DslDrivenSpec::class.bestName()
      })
   }
}

class InvalidDslException(message: String) : Exception(message)
