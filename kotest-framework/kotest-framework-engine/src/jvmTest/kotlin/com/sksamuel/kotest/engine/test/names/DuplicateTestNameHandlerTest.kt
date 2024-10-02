package com.sksamuel.kotest.engine.test.names

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestName
import io.kotest.engine.test.names.DuplicateTestNameException
import io.kotest.engine.test.names.DuplicateTestNameHandler
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class DuplicateTestNameHandlerTest : FunSpec({

   test("in warn mode duplicate names should be renamed") {
      val handler = DuplicateTestNameHandler(DuplicateTestNameMode.Warn)
      handler.handle(TestName("foo")) shouldBe null
      handler.handle(TestName("foo")) shouldBe "(1) foo"
      handler.handle(TestName("foo")) shouldBe "(2) foo"
   }

   test("in silent mode duplicate names should be renamed") {
      val handler = DuplicateTestNameHandler(DuplicateTestNameMode.Silent)
      handler.handle(TestName("foo")) shouldBe null
      handler.handle(TestName("foo")) shouldBe "(1) foo"
      handler.handle(TestName("foo")) shouldBe "(2) foo"
   }

   test("in error mode duplicate names should throw DuplicateTestNameException") {
      val handler = DuplicateTestNameHandler(DuplicateTestNameMode.Error)
      handler.handle(TestName("foo")) shouldBe null
      shouldThrow<DuplicateTestNameException> {
         handler.handle(TestName("foo")) shouldBe "(1) foo"
      }
   }
})
