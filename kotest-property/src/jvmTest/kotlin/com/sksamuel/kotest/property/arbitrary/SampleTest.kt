package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.bind

class SampleTest: StringSpec() {
   init {
       "asd" {
          val arb = Arb.bind<Foo>()

          val rs = RandomSource.default()
          val message = arb.sample(rs)
          println(message)
       }
      "wertw" {
         val arb = Arb.bind<T>()

         println(arb.sample(RandomSource.default()))
      }
   }

   data class T(val s: Array<String>)

   data class Foo(var bb:ByteArray)
}


