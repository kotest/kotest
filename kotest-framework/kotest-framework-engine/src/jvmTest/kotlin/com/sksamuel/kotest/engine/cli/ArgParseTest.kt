package com.sksamuel.kotest.engine.cli

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.cli.Arg
import io.kotest.engine.cli.parseArgs
import io.kotest.matchers.shouldBe

class ArgParseTest : FunSpec() {
   init {
      test("parsing args happy path") {
         val args = listOf("--reporter", "taycan", "--package", "com.foo.bar.baz", "--spec", "FooBarTest")
         parseArgs(args) shouldBe listOf(
            Arg("reporter", "taycan"),
            Arg("package", "com.foo.bar.baz"),
            Arg("spec", "FooBarTest"),
         )
      }

      test("parsing args with spaces in a value mid stream") {
         val args = listOf("--reporter", "taycan", "--testname", "my test should be great", "--spec", "FooBarTest")
         parseArgs(args) shouldBe listOf(
            Arg("reporter", "taycan"),
            Arg("testname", "my test should be great"),
            Arg("spec", "FooBarTest"),
         )
      }

      test("parsing args with spaces in the final element") {
         val args = listOf("--reporter", "taycan", "--spec", "FooBarTest", "--testname", "my test should be great")
         parseArgs(args) shouldBe listOf(
            Arg("reporter", "taycan"),
            Arg("spec", "FooBarTest"),
            Arg("testname", "my test should be great"),
         )
      }

      test("parsing args with non alpha") {
         val args = listOf("--reporter", "taycan", "--spec", "FooBarTest", "--tags", "(Linux || Windows) && Hive")
         parseArgs(args) shouldBe listOf(
            Arg("reporter", "taycan"),
            Arg("spec", "FooBarTest"),
            Arg("tags", "(Linux || Windows) && Hive"),
         )
      }

      test("parsing args with empty value") {
         val args = listOf("--reporter", "taycan", "--spec", "FooBarTest", "--tags", "--testname", "wibble test")
         parseArgs(args) shouldBe listOf(
            Arg("reporter", "taycan"),
            Arg("spec", "FooBarTest"),
            Arg("tags", ""),
            Arg("testname", "wibble test"),
         )
      }
   }
}
