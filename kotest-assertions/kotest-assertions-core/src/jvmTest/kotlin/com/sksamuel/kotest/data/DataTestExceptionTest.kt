package com.sksamuel.kotest.data

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.string.shouldContain

@EnabledIf(LinuxOnlyGithubCondition::class)
class DataTestExceptionTest : FunSpec({
   test("failure in forAll should keep original stack trace") {
      val t = shouldThrowAny {
         forAll(
            row("a"),
            row("simple"),
            row("data test")
         ) { a ->
            a.toInt()
         }
      }
      t.message shouldContain """Test failed for (a, "a") with error java.lang.NumberFormatException: For input string: "a""""
      t.message shouldContain """Test failed for (a, "simple") with error java.lang.NumberFormatException: For input string: "simple""""
      t.message shouldContain """Test failed for (a, "data test") with error java.lang.NumberFormatException: For input string: "data test""""
   }
})
