package io.kotest.samples.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.string.shouldNotBeBlank

class FunSpecCallbackExample : FunSpec({

   beforeTest {

   }

   afterTest {

   }

   include(myfactory)
   include(myfactory2())

   test("a string cannot be blank") {
      "wibble".shouldNotBeBlank()
   }

})

fun myfactory2() = funSpec {}

val myfactory = funSpec {
   test("foo") {

   }
}

