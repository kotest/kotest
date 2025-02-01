package com.sksamuel.kt.koin

import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.koin.core.context.GlobalContext
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock

class KoinListenerMockTest : FunSpec(), KoinTest {

   init {
      val genericService by inject<GenericService>()

      extension(KoinExtension(koinModule) { mockk<GenericRepository>() })

      test("Should allow mocking correctly") {

         declareMock<GenericRepository> {
            every { foo() } returns "DootyDoot"
         }

         genericService.foo() shouldBe "DootyDoot"
      }

      afterSpec {
         GlobalContext.getOrNull() shouldBe null // We should finish koin after test execution
      }
   }
}
