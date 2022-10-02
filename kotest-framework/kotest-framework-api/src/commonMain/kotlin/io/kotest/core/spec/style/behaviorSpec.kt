package io.kotest.core.spec.style

import io.kotest.common.ExperimentalKotest
import io.kotest.core.factory.TestFactory
import io.kotest.core.factory.TestFactoryConfiguration
import io.kotest.core.factory.build
import io.kotest.core.names.TestName
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.style.scopes.BehaviorSpecGivenContainerScope
import io.kotest.core.spec.style.scopes.BehaviorSpecRootScope
import io.kotest.core.spec.style.scopes.BehaviorSpecWhenContainerScope
import io.kotest.core.spec.style.scopes.ContainerScope

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

abstract class BehaviorSpec(body: BehaviorSpec.() -> Unit = {}) : DslDrivenSpec(), BehaviorSpecRootScope {
   init {
      body()
   }

   /**
    * Adds a [BehaviorSpecGivenContainerScope] to this container.
    */
   @Suppress("FunctionName")
   @ExperimentalKotest
   suspend fun ContainerScope.Given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      registerContainer(
         TestName("Given: ", name, true),
         disabled = false,
         null,
      ) { BehaviorSpecGivenContainerScope(this).test() }

   /**
    * Adds a [BehaviorSpecGivenContainerScope] to this container.
    */
   @ExperimentalKotest
   suspend fun ContainerScope.given(name: String, test: suspend BehaviorSpecGivenContainerScope.() -> Unit) =
      registerContainer(
         TestName("Given: ", name, true),
         disabled = false,
         null,
      ) { BehaviorSpecGivenContainerScope(this).test() }

   /**
    * Adds a [BehaviorSpecWhenContainerScope] to this container.
    */
   @Suppress("FunctionName")
   suspend fun ContainerScope.When(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      registerContainer(
         TestName("When: ", name, true),
         disabled = false,
         null,
      ) { BehaviorSpecWhenContainerScope(this).test() }

   /**
    * Adds a [BehaviorSpecWhenContainerScope] to this container.
    */
   suspend fun ContainerScope.`when`(name: String, test: suspend BehaviorSpecWhenContainerScope.() -> Unit) =
      registerContainer(
         TestName("When: ", name, true),
         disabled = false,
         null,
      ) { BehaviorSpecWhenContainerScope(this).test() }
}
