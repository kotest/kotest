package com.sksamuel.kotest.eq

import io.kotest.assertions.eq.ThrowableEq
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

class ThrowableEqTest : FunSpec({
   test("should give null if two given throwable have same message, cause and class") {
      val cause = Throwable("Root cause")
      ThrowableEq.equals(
         TestThrowable("Connection error", cause),
         TestThrowable("Connection error", cause)
      ).shouldBeNull()
   }

   test("should give give error when two throwable have different error message") {
      val cause = Throwable("Root cause")
      ThrowableEq.equals(
         TestThrowable("Linkage error", cause),
         TestThrowable("Connection error", cause)
      ).shouldNotBeNull()
   }

   test("should give give error when two throwable have different cause") {
      ThrowableEq.equals(
         TestThrowable("Linkage error", Throwable("Root cause")),
         TestThrowable("Linkage error", Throwable("Connection Root cause"))
      ).shouldNotBeNull()
   }

   test("should give give error when two throwable have different class but same cause and error message") {
      val rootCause = Throwable("Root cause")
      ThrowableEq.equals(
         TestThrowable("Linkage error", rootCause),
         AnotherTestThrowable("Linkage error", rootCause)
      ).shouldNotBeNull()
   }
})

class TestThrowable(override val message: String?, override val cause: Throwable?) : Throwable()
class AnotherTestThrowable(override val message: String?, override val cause: Throwable?) : Throwable()
