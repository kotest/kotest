package com.sksamuel.kotest.engine.spec

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.AbstractSpec
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.DelegatingTestScope
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestRunnable
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType

abstract class SuiteSpec : AbstractSpec() {

   @TestRunnable
   fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
      add(
         RootTest(
            name = TestNameBuilder.builder(name).build(),
            test = { SuiteScope(this).test() },
            type = TestType.Container,
            source = sourceRef(),
            xmethod = TestXMethod.NONE,
            config = null,
            factoryId = null
         )
      )
   }

   @TestRunnable
   fun test(name: String, test: suspend TestScope.() -> Unit) {
      add(
         RootTest(
            name = TestNameBuilder.builder(name).build(),
            test = test,
            type = TestType.Test,
            source = sourceRef(),
            xmethod = TestXMethod.NONE,
            config = null,
            factoryId = null
         )
      )
   }
}

class SuiteScope(
   val testScope: TestScope,
) : DelegatingTestScope(testScope) {

   @TestRunnable
   suspend fun suite(name: String, test: suspend SuiteScope.() -> Unit) {
      testScope.registerTestCase(
         NestedTest(
            name = TestNameBuilder.builder(name).build(),
            test = { SuiteScope(this).test() },
            type = TestType.Test,
            source = sourceRef(),
            xmethod = TestXMethod.NONE,
            config = null,
         )
      )
   }

   @TestRunnable
   suspend fun test(name: String, test: suspend TestScope.() -> Unit) {
      testScope.registerTestCase(
         NestedTest(
            name = TestNameBuilder.builder(name).build(),
            test = test,
            type = TestType.Test,
            source = sourceRef(),
            xmethod = TestXMethod.NONE,
            config = null,
         )
      )
   }
}
