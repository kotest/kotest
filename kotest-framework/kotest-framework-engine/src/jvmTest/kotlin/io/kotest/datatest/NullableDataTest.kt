package io.kotest.datatest

import io.kotest.core.spec.style.FunSpec

class NullableDataTest : FunSpec() {
   init {

      val listOfData = listOf(
         "one",
         "two",
         null
      )

      context("data tests should support nulls") {
         withData<String?,_>({ "Test with $it" }, listOfData) { }
      }

      withData<String?,_>({ "Test with $it" }, listOfData) { }
   }
}
