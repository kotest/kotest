@file:Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")

package com.sksamuel.kotest.assertions

import io.kotest.assertions.MultiAssertionError
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.Issue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotEndWith

@Issue("https://github.com/kotest/kotest/issues/5685")
class AssertSoftlyJSTest : FunSpec() {
   init {
      test("assertSoftly with normal matchers") {
         shouldThrow<MultiAssertionError> {
            assertSoftly {
               "Expect failure 1!" shouldEndWith "a"
               "Expect failure 2!" shouldEndWith "b"
            }
         }.message shouldContain "The following 2 assertions failed:"
      }

      test("assertSoftly with inverted matchers") {
         shouldThrow<MultiAssertionError> {
            assertSoftly {
               "Expect failure 1!" shouldNotEndWith "!"
               "Expect failure 2!" shouldNotEndWith "!"
            }
         }.message shouldContain "The following 2 assertions failed:"
      }
   }
}
