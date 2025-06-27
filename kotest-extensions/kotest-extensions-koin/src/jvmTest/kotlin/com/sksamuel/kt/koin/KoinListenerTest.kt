package com.sksamuel.kt.koin

import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.koin.KoinExtension
import io.kotest.koin.KoinPropTestListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.PropTestConfig
import io.kotest.property.checkAll
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.inject

private class MyService(val a: Boolean)

private val myModule = module {
   factory { (a: Boolean) -> MyService(a) }
}

class KoinListenerTest : FunSpec(), KoinTest {

   private val genericService by inject<GenericService>()

   init {

      this@KoinListenerTest.extensions(KoinExtension(koinModule))

      test("Should have autowired the service correctly") {
         genericService.foo() shouldBe "Bar"
      }

      test("KoinPropTestListener should reset koin context for each property test iteration") {
         var previousService: MyService? = null
         checkAll<Boolean>(
            PropTestConfig(
               listeners = listOf(KoinPropTestListener(myModule))
            )
         ) { a: Boolean ->
            val currentService: MyService = get { parametersOf(a) }
            currentService.a shouldBe a
            if (previousService != null) {
               currentService shouldNotBe previousService
            }
            previousService = currentService
         }
      }
   }

   override suspend fun afterSpec(spec: Spec) {
      GlobalContext.getOrNull() shouldBe null // We should finish koin after test execution
   }
}
