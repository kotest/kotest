package com.sksamuel.kotest.engine.cli

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.cli.parseArgs
import io.kotest.matchers.shouldBe

class ArgParseTest : FunSpec() {
   init {

      test("parsing args happy path") {
         val args = listOf("--reporter", "taycan", "--package", "com.foo.bar.baz", "--spec", "FooBarTest")
         parseArgs(args) shouldBe mapOf(
            "reporter" to "taycan",
            "package" to "com.foo.bar.baz",
            "spec" to "FooBarTest",
         )
      }

      test("parsing args with descriptor") {
         val args = listOf(
            "--listener",
            "teamcity",
            "--termcolor",
            "ansi16",
            "--candidates",
            "com.sksamuel.aedile.core.CoroutineContextTest",
            "--descriptor",
            "com.sksamuel.aedile.core.CoroutineContextTest/the calling context should be used by default for loading caches"
         )
         parseArgs(args) shouldBe mapOf(
            "listener" to "teamcity",
            "termcolor" to "ansi16",
            "candidates" to "com.sksamuel.aedile.core.CoroutineContextTest",
            "descriptor" to "com.sksamuel.aedile.core.CoroutineContextTest/the calling context should be used by default for loading caches",
         )
      }

      test("parsing args with spaces in a value mid stream") {
         val args = listOf("--reporter", "taycan", "--testname", "my test should be great", "--spec", "FooBarTest")
         parseArgs(args) shouldBe mapOf(
            "reporter" to "taycan",
            "testname" to "my test should be great",
            "spec" to "FooBarTest",
         )
      }

      test("parsing args with spaces in the final element") {
         val args = listOf("--reporter", "taycan", "--spec", "FooBarTest", "--testname", "my test should be great")
         parseArgs(args) shouldBe mapOf(
            "reporter" to "taycan",
            "spec" to "FooBarTest",
            "testname" to "my test should be great",
         )
      }

      test("parsing args with non alpha") {
         val args = listOf("--reporter", "taycan", "--spec", "FooBarTest", "--tags", "(Linux || Windows) && Hive")
         parseArgs(args) shouldBe mapOf(
            "reporter" to "taycan",
            "spec" to "FooBarTest",
            "tags" to "(Linux || Windows) && Hive",
         )
      }

      test("parsing args with empty value") {
         val args = listOf("--reporter", "taycan", "--candidates", "FooBarTest", "--tags", "--testname", "wibble test")
         parseArgs(args) shouldBe mapOf(
            "reporter" to "taycan",
            "candidates" to "FooBarTest",
            "tags" to "",
            "testname" to "wibble test",
         )
      }
   }
}
