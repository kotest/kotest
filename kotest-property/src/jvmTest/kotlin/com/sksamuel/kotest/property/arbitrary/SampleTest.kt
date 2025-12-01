package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.shouldBePositive
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.intArray
import io.kotest.property.arbitrary.take
import io.kotest.property.checkAll
import io.kotest.property.forAll

class SampleTest: StringSpec() {
   init {
//      "wert" {
//         Arb.bind<ByteArray>().checkAll(10) {
//            println(it)
//         }
//      }
      "adfgasdfg" {
         val arb = Arb.intArray(Arb.int(10..15), Arb.int())
         arb.take(10).forEach { println(it.joinToString(",")) }
//         arb.checkAll(10) {
//            println(it.joinToString(","))
//         }
      }
       "asd" {
//          Arb.bind<Foo>().checkAll(10) {
//             println(it)
//          }
//          forAll(10, Arb.bind<Foo>()) {
//             println(it)
//             true
//          }
          assertSoftly(Arb.bind<Foo>().sample(RandomSource.default())) {
             it.value.bb.size.shouldBePositive()
          }

//          val rs = RandomSource.default()
//          val message = arb.sample(rs)
//          println(message)
       }
//      "wertw" {
//         val arb = Arb.bind<T>()
//
//         println(arb.sample(RandomSource.default()))
//      }
   }

   data class T(val s: Array<String>)

   data class Foo(var bb:ByteArray)
}


