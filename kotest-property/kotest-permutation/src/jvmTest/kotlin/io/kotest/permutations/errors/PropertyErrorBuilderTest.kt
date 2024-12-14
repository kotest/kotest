package io.kotest.permutations.errors

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.permutations.Input

class PropertyErrorBuilderTest : FunSpec() {
   init {

      test("should include seed") {
         PropertyErrorMessageBuilder.builder(12, UnsupportedOperationException())
            .withSeed(32432)
            .build() shouldContain """Repeat this test by using seed 32432"""
      }

      test("should include last inputs") {
         PropertyErrorMessageBuilder.builder(12, UnsupportedOperationException())
            .withSeed(32432)
           .withInputs(listOf(Input("a", "1232"), Input("b", "qewqe")))
            .build() shouldContain """Inputs:
a) 1232
b) qewqe"""
      }


   }
}
