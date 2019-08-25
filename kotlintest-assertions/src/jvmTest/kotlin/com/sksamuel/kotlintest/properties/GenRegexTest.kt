package com.sksamuel.kotlintest.properties

import io.kotlintest.inspectors.forAll
import io.kotlintest.matchers.string.shouldMatch
import io.kotlintest.properties.Gen
import io.kotlintest.properties.regex
import io.kotlintest.specs.FunSpec

class GenRegexTest : FunSpec({

   test("regex generation") {
      val regex = "a.b.\\d{2}"
      Gen.regex(regex).random().take(100).forAll { it.shouldMatch(regex) }
   }

   test("regex generation 2") {
      val regex = "[a-z]-[0-9]abbc."
      Gen.regex(regex).random().take(100).forAll { it.shouldMatch(regex) }
   }
})
