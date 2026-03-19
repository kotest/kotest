package com.sksamuel.foo

import io.kotest.core.spec.style.FunSpec
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MyTests : FunSpec({

   listener(myListener)

   beforeSpec {
   }

   afterSpec {
   }

   test("foo") {

   }

   include(aTestFactoryFunction())
   include(aTestFactionValue)
   include(bTestFactionValue)
   include(cTestFactionValue)
   include(dTestFactoryFunction())
})
