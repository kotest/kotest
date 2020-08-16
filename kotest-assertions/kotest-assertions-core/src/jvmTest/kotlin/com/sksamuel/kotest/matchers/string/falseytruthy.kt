package com.sksamuel.kotest.matchers.string

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beFalsy
import io.kotest.matchers.string.beTruthy
import io.kotest.matchers.string.shouldBeFalsy
import io.kotest.matchers.string.shouldBeTruthy

class FalseyTruthyTest : FreeSpec({

   "should be truthy" - {
      "should work with proper values" {
         "true".shouldBeTruthy()
         "yes".shouldBeTruthy()
         "y".shouldBeTruthy()
         "1".shouldBeTruthy()
         "Y".shouldBeTruthy()
         "Yes".shouldBeTruthy()
         "YeS".shouldBeTruthy()
         "True".shouldBeTruthy()
         "TrUe".shouldBeTruthy()
         "TRUE".shouldBeTruthy()
      }

      "should fail with unexpected values" {
         shouldThrow<AssertionError> { "false".shouldBeTruthy() }
         shouldThrow<AssertionError> { "no".shouldBeTruthy() }
         shouldThrow<AssertionError> { "n".shouldBeTruthy() }
         shouldThrow<AssertionError> { "0".shouldBeTruthy() }
         shouldThrow<AssertionError> { "whatever".shouldBeTruthy() }
         shouldThrow<AssertionError> { "true yes".shouldBeTruthy() }
         shouldThrow<AssertionError> { "a".shouldBeTruthy() }
         shouldThrow<AssertionError> { "10".shouldBeTruthy() }
         shouldThrow<AssertionError> { "".shouldBeTruthy() }
         shouldThrow<AssertionError> { " ".shouldBeTruthy() }
         shouldThrow<AssertionError> { " ".shouldBeTruthy() }
         shouldThrow<AssertionError> { null.shouldBeTruthy() }
      }

      "should provide error message" {
         shouldThrow<AssertionError> { "false".shouldBeTruthy() }
            .message.shouldBe("\"false\" should be equal (ignoring case) to one of: [true, yes, y, 1]")
         shouldThrow<AssertionError> { "YES" shouldNot beTruthy() }
            .message.shouldBe("\"YES\" should not be equal (ignoring case) to one of: [true, yes, y, 1]")
      }
   }

   "should be falsy" - {
      "should work with proper values" {
         "false".shouldBeFalsy()
         "no".shouldBeFalsy()
         "n".shouldBeFalsy()
         "0".shouldBeFalsy()
         "N".shouldBeFalsy()
         "No".shouldBeFalsy()
         "nO".shouldBeFalsy()
         "False".shouldBeFalsy()
         "FaLse".shouldBeFalsy()
         "FALSE".shouldBeFalsy()
      }

      "should fail with unexpected values" {
         shouldThrow<AssertionError> { "true".shouldBeFalsy() }
         shouldThrow<AssertionError> { "yes".shouldBeFalsy() }
         shouldThrow<AssertionError> { "1".shouldBeFalsy() }
         shouldThrow<AssertionError> { "y".shouldBeFalsy() }
         shouldThrow<AssertionError> { "whatever".shouldBeFalsy() }
         shouldThrow<AssertionError> { "true yes".shouldBeFalsy() }
         shouldThrow<AssertionError> { "a".shouldBeFalsy() }
         shouldThrow<AssertionError> { "10".shouldBeFalsy() }
         shouldThrow<AssertionError> { "".shouldBeFalsy() }
         shouldThrow<AssertionError> { " ".shouldBeFalsy() }
         shouldThrow<AssertionError> { " ".shouldBeFalsy() }
         shouldThrow<AssertionError> { null.shouldBeFalsy() }
      }

      "should provide error message" {
         shouldThrow<AssertionError> { "yes".shouldBeFalsy() }
            .message.shouldBe("\"yes\" should be equal (ignoring case) to one of: [false, no, n, 0]")
         shouldThrow<AssertionError> { "FALSE" shouldNot beFalsy() }
            .message.shouldBe("\"FALSE\" should not be equal (ignoring case) to one of: [false, no, n, 0]")
      }
   }


})
