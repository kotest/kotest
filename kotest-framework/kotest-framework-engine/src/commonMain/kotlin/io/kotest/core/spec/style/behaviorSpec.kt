package io.kotest.core.spec.style

import io.kotest.common.ExperimentalKotest
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.AbstractSpec
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.scopes.BehaviorSpecContextContainerScope
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerScope
import io.kotest.core.spec.style.scopes.BehaviorSpecRootScope
import io.kotest.core.spec.style.scopes.BehaviorSpecWhenContainerScope
import io.kotest.core.spec.style.scopes.ContainerScope
import io.kotest.core.test.TestType

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [BehaviorSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'behavior-spec' style.
 */
fun behaviorSpec(block: BehaviorSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = BehaviorSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

class BehaviorSpecTestFactoryConfiguration : TestFactoryConfiguration(), BehaviorSpecRootScope

abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : AbstractSpec(), BehaviorSpecRootScope {
   init {
      body()
   }

   /**
    * Adds a [BehaviorSpecContextContainerScope] to this container.
    */
   @Suppress("FunctionName")
   suspend fun ContainerScope.Context(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      registerTest(
         TestDefinitionBuilder.builder(
            TestNameBuilder.builder(name).withPrefix("Context: ").withDefaultAffixes().build(),
            TestType.Container
         ).withXmethod(TestXMethod.NONE).build { BehaviorSpecContextContainerScope(this).test() }
      )

   /**
    * Adds a [BehaviorSpecContextContainerScope] to this container.
    */
   suspend fun ContainerScope.context(name: String, test: suspend BehaviorSpecContextContainerScope.() -> Unit) =
      registerTest(
         TestDefinitionBuilder.builder(
            TestNameBuilder.builder(name).withPrefix("Context: ").withDefaultAffixes().build(),
            TestType.Container
         ).withXmethod(TestXMethod.NONE).build { BehaviorSpecContextContainerScope(this).test() }
      )

   /**
    * Adds a [BehaviorSpecGivenContainerScope] to this container.
    */
   @Suppress("FunctionName")
   suspend fun ContainerScope.Given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      registerTest(
         TestDefinitionBuilder.builder(
            TestNameBuilder.builder(name).withPrefix("Given: ").withDefaultAffixes().build(),
            TestType.Container
         ).withXmethod(TestXMethod.NONE).build { BehaviorSpecGivenContainerScope(this).test() }
      )

   /**
    * Adds a [BehaviorSpecGivenContainerScope] to this container.
    */
   @ExperimentalKotest
   suspend fun ContainerScope.given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      registerTest(
         TestDefinitionBuilder.builder(
            TestNameBuilder.builder(name).withPrefix("Given: ").withDefaultAffixes().build(),
            TestType.Container
         ).withXmethod(TestXMethod.NONE).build { BehaviorSpecGivenContainerScope(this).test() }
      )

   /**
    * Adds a [BehaviorSpecWhenContainerScope] to this container.
    */
   @Suppress("FunctionName")
   suspend fun ContainerScope.When(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      registerTest(
         TestDefinitionBuilder.builder(
            TestNameBuilder.builder(name).withPrefix("When: ").withDefaultAffixes().build(),
            TestType.Container
         ).withXmethod(TestXMethod.NONE).build { BehaviorSpecWhenContainerScope(this).test() }
      )

   /**
    * Adds a [BehaviorSpecWhenContainerScope] to this container.
    */
   suspend fun ContainerScope.`when`(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      registerTest(
         TestDefinitionBuilder.builder(
            TestNameBuilder.builder(name).withPrefix("When: ").withDefaultAffixes().build(),
            TestType.Container
         ).withXmethod(TestXMethod.NONE).build { BehaviorSpecWhenContainerScope(this).test() }
      )
}
