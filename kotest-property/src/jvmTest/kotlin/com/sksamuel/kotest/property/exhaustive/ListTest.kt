package com.sksamuel.kotest.property.exhaustive

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.property.exhaustive.exhaustive

@EnabledIf(LinuxCondition::class)
class ListTest : FunSpec({

   test("successfully build a exhaustive with a list as receiver") {
      listOf("a").exhaustive().values shouldHaveSingleElement "a"
   }

   test("successfully build a exhaustive with list as argument") {
      exhaustive(listOf("b")).values shouldHaveSingleElement "b"
   }

   test("should throws for a empty list as receiver") {
      shouldThrow<IllegalArgumentException> {
         emptyList<Int>().exhaustive()
      }
   }

   test("should throws for empty list as argument") {
      shouldThrow<IllegalArgumentException> {
         exhaustive(emptyList<Int>())
      }
   }
})
