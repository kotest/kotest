package com.sksamuel.kotest.engine.test.names

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.test.names.DuplicateTestNameException
import io.kotest.engine.test.names.DuplicateTestNameHandler
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class DuplicateTestNameHandlerTest : FunSpec({

   test("in warn mode duplicate names should be renamed") {
      val handler = DuplicateTestNameHandler()
      handler.handle(DuplicateTestNameMode.Warn, TestNameBuilder.builder("foo").build()) shouldBe null
      handler.handle(DuplicateTestNameMode.Warn, TestNameBuilder.builder("foo").build()) shouldBe "(1) foo"
      handler.handle(DuplicateTestNameMode.Warn, TestNameBuilder.builder("foo").build()) shouldBe "(2) foo"
   }

   test("in silent mode duplicate names should be renamed") {
      val handler = DuplicateTestNameHandler()
      handler.handle(DuplicateTestNameMode.Silent, TestNameBuilder.builder("foo").build()) shouldBe null
      handler.handle(DuplicateTestNameMode.Silent, TestNameBuilder.builder("foo").build()) shouldBe "(1) foo"
      handler.handle(DuplicateTestNameMode.Silent, TestNameBuilder.builder("foo").build()) shouldBe "(2) foo"
   }

   test("in error mode duplicate names should throw DuplicateTestNameException") {
      val handler = DuplicateTestNameHandler()
      handler.handle(DuplicateTestNameMode.Error, TestNameBuilder.builder("foo").build()) shouldBe null
      shouldThrow<DuplicateTestNameException> {
         handler.handle(DuplicateTestNameMode.Error, TestNameBuilder.builder("foo").build()) shouldBe "(1) foo"
      }
   }
})
