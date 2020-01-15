package com.sksamuel.kotest.tests.konform

import io.konform.validation.Validation
import io.konform.validation.jsonschema.maxLength
import io.konform.validation.jsonschema.maximum
import io.konform.validation.jsonschema.minLength
import io.konform.validation.jsonschema.minimum
import io.kotest.assertions.konform.shouldBeInvalid
import io.kotest.assertions.konform.shouldBeValid
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.shouldThrow
import java.lang.AssertionError

class ValidatedMatchersTest : StringSpec({
   val validateUser = Validation<UserProfile> {
      UserProfile::fullName {
         minLength(2)
         maxLength(100)
      }

      UserProfile::age ifPresent {
         minimum(0)
         maximum(150)
      }
   }

   "shouldBeValid" {
      val validUser = UserProfile("Alice", 25)

      validateUser shouldBeValid validUser
      shouldThrow<AssertionError> {
         validateUser shouldBeInvalid validUser
      }
   }

   "shouldNotBeValid" {
      val invalidUser = UserProfile("A", -1)

      validateUser.shouldBeInvalid(invalidUser) { invalid ->
         invalid[UserProfile::fullName].let {
            it.shouldNotBeNull()
            it shouldContain "must have at least 2 characters"
         }
         invalid[UserProfile::age].let {
            it.shouldNotBeNull()
            it shouldContain "must be at least '0'"
         }
      }
      shouldThrow<AssertionError> {
         validateUser shouldBeValid invalidUser
      }
   }

})

private data class UserProfile(
   val fullName: String,
   val age: Int?
)
