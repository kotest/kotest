package io.kotest.datatest

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

class DataTestingRepeatedTestNameTest : FunSpec() {
   init {

      isolationMode = IsolationMode.InstancePerLeaf

      test("with describe spec repeated names should have count appended") {

         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(RepeatedNamesDescribeSpec::class)
            .launch()

         collector.names shouldBe listOf(
            "Foo(name=sam)",
            "Foo(name=ham)",
            "Foo(name=sham)",
            "(1) Foo(name=sham)",
            "(1) Foo(name=ham)",
            "(2) Foo(name=ham)",
            "(1) Foo(name=sam)",
            "foo"
         )
      }

      test("with describe spec repeated names at root should have count appended") {

         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(RepeatedNamesDescribeSpecRoot::class)
            .launch()

         collector.names shouldBe listOf(
            "Foo(name=sam)",
            "Foo(name=ham)",
            "Foo(name=sham)",
            "(1) Foo(name=sham)",
            "(1) Foo(name=ham)",
            "(2) Foo(name=ham)",
            "(1) Foo(name=sam)",
         )
      }

      test("with fun spec repeated names should have count appended") {

         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(RepeatedNamesFunSpec::class)
            .launch()

         collector.names shouldBe listOf(
            "Foo(name=sam)",
            "Foo(name=ham)",
            "Foo(name=sham)",
            "(1) Foo(name=sham)",
            "(1) Foo(name=ham)",
            "(2) Foo(name=ham)",
            "(1) Foo(name=sam)",
            "foo"
         )
      }

      test("with fun spec repeated names at root should have count appended") {

         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(RepeatedNamesRootFunSpec::class)
            .launch()

         collector.names shouldBe listOf(
            "Foo(name=sam)",
            "Foo(name=ham)",
            "Foo(name=sham)",
            "(1) Foo(name=sham)",
            "(1) Foo(name=ham)",
            "(2) Foo(name=ham)",
            "(1) Foo(name=sam)",
         )
      }
   }
}

private class RepeatedNamesDescribeSpecRoot : DescribeSpec() {
   init {
      withData(
         Foo("sam"),
         Foo("ham"),
         Foo("sham"),
         Foo("sham"),
         Foo("ham"),
         Foo("ham"),
         Foo("sam"),
      ) { }
   }
}

private class RepeatedNamesDescribeSpec : DescribeSpec() {
   init {
      describe("foo") {
         withData(
            Foo("sam"),
            Foo("ham"),
            Foo("sham"),
            Foo("sham"),
            Foo("ham"),
            Foo("ham"),
            Foo("sam"),
         ) { }
      }
   }
}

private class RepeatedNamesFunSpec : FunSpec() {
   init {
      context("foo") {
         withData(
            Foo("sam"),
            Foo("ham"),
            Foo("sham"),
            Foo("sham"),
            Foo("ham"),
            Foo("ham"),
            Foo("sam"),
         ) { }
      }
   }
}

private class RepeatedNamesRootFunSpec : FunSpec() {
   init {
      withData(
         Foo("sam"),
         Foo("ham"),
         Foo("sham"),
         Foo("sham"),
         Foo("ham"),
         Foo("ham"),
         Foo("sam"),
      ) { }
   }
}


private data class Foo(val name: String)
