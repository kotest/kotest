package com.sksamuel.kotest.tag

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.InvalidDslException
import io.kotest.core.spec.style.FunSpec

class SealedTagsTest : FunSpec() {
   init {
      test("do not allow tags after spec is initialized") {
         shouldThrow<InvalidDslException> {
            tags(Exclude1)
         }
      }
   }
}
