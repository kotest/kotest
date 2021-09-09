package com.sksamuel.kotest.engine.test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.DuplicateTestNameMode
import io.kotest.core.test.createTestName
import io.kotest.engine.test.DuplicateTestNameException
import io.kotest.engine.test.DuplicateTestNameHandler
import io.kotest.matchers.shouldBe

class DuplicateTestNameHandlerTest : FunSpec({

   test("in warn mode duplicate names should be renamed") {
      val handler = DuplicateTestNameHandler(DuplicateTestNameMode.Warn)
      handler.handle(createTestName("foo")) shouldBe null
      handler.handle(createTestName("foo")) shouldBe "(1) foo"
      handler.handle(createTestName("foo")) shouldBe "(2) foo"
   }

   test("in silent mode duplicate names should be renamed") {
      val handler = DuplicateTestNameHandler(DuplicateTestNameMode.Silent)
      handler.handle(createTestName("foo")) shouldBe null
      handler.handle(createTestName("foo")) shouldBe "(1) foo"
      handler.handle(createTestName("foo")) shouldBe "(2) foo"
   }

   test("in error mode duplicate names should throw DuplicateTestNameException") {
      val handler = DuplicateTestNameHandler(DuplicateTestNameMode.Error)
      handler.handle(createTestName("foo")) shouldBe null
      shouldThrow<DuplicateTestNameException> {
         handler.handle(createTestName("foo")) shouldBe "(1) foo"
      }
   }
})
