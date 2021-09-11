package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.default
import io.kotest.property.internal.proptest

abstract class PropSpec(body: PropSpec.() -> Unit = {}) : FunSpec() {

   init {
      body()
   }

   inline fun <reified A> prop(
      name: String,
      noinline property: suspend PropertyContext.(A) -> Unit
   ) {
      test(name) {
         proptest(
            Arb.default(),
            PropTestConfig(),
            property
         )
      }
   }

   fun <A> prop(
      name: String,
      arbA: Arb<A>,
      property: suspend PropertyContext.(A) -> Unit
   ) {
      test(name) {
         proptest(
            arbA,
            PropTestConfig(),
            property
         )
      }
   }

   inline fun <reified A, reified B> prop(
      name: String,
      noinline property: suspend PropertyContext.(A, B) -> Unit
   ) {
      test(name) {
         proptest(
            Arb.default(),
            Arb.default(),
            PropTestConfig(),
            property
         )
      }
   }
}
