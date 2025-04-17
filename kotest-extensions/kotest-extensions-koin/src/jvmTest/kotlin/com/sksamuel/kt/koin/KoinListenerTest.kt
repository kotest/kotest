package com.sksamuel.kt.koin

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import org.koin.core.context.GlobalContext
import org.koin.test.KoinTest
import org.koin.test.inject

class KoinListenerTest : FunSpec(), KoinTest {

   private val genericService by inject<GenericService>()

   init {

      this@KoinListenerTest.extensions(KoinExtension(koinModule))

      test("Should have autowired the service correctly") {
         genericService.foo() shouldBe "Bar"
      }
   }

   override suspend fun afterSpec(spec: Spec) {
      GlobalContext.getOrNull() shouldBe null // We should finish koin after test execution
   }
}
