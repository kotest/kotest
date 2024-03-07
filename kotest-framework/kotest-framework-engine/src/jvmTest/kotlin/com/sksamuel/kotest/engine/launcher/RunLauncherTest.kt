package com.sksamuel.kotest.engine.launcher

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.launcher.LauncherArgs
import io.kotest.engine.launcher.setupLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class RunLauncherTest: FunSpec() {
   init {
      test("runLauncher should run tests for known class") {
         val listener = SetupLauncherTestListener()
         val result = setupLauncher(
            LauncherArgs(
            null,
            "com.sksamuel.kotest.engine.launcher",
            "com.sksamuel.kotest.engine.launcher.DescribeSpec1",
            null, null, null), listener)
         result.isFailure.shouldBeFalse()
         result.getOrNull()?.launch()
         listener.testFinished.sorted() shouldBe arrayOf(
            "bar", "first", "foo", "nothing else", "second", "second bar", "something", "something else"
         )
         listener.specFinished.sorted() shouldBe arrayOf("DescribeSpec1")
      }

      test("runLauncher should support testpath") {
         val listener = SetupLauncherTestListener()
         val result = setupLauncher(
            LauncherArgs(
            "something",
            "com.sksamuel.kotest.engine.launcher",
            "com.sksamuel.kotest.engine.launcher.DescribeSpec1",
            null, null, null), listener)
         result.isFailure.shouldBeFalse()
         result.getOrNull()?.launch()
         listener.testStarted.size shouldBe 3
         listener.testStarted.filter{it.type == TestType.Container}.map { it.descriptor.id.value } shouldBe arrayOf("something")
         listener.testStarted.filter{it.type == TestType.Test}.map { it.descriptor.id.value } shouldBe arrayOf("first", "second")
      }

      test("runLauncher should support single level testpath regex") {
         val listener = SetupLauncherTestListener()
         val result = setupLauncher(
            LauncherArgs(
               "something*",
               "com.sksamuel.kotest.engine.launcher",
               "com.sksamuel.kotest.engine.launcher.DescribeSpec1",
               null, null, null), listener)
         result.isFailure.shouldBeFalse()
         result.getOrNull()?.launch()
         listener.testStarted.filter{it.type == TestType.Container}.map { it.descriptor.id.value } shouldBe arrayOf("something", "something else")
         listener.testStarted.filter{it.type == TestType.Test}.map { it.descriptor.id.value } shouldBe arrayOf("first", "second", "foo")
      }

      test("runLauncher should support multi level testpath regex") {
         val listener = SetupLauncherTestListener()
         val result = setupLauncher(
            LauncherArgs(
               "something* -- first",
               "com.sksamuel.kotest.engine.launcher",
               "com.sksamuel.kotest.engine.launcher.DescribeSpec1",
               null, null, null), listener)
         result.isFailure.shouldBeFalse()
         result.getOrNull()?.launch()
         listener.testStarted.filter{it.type == TestType.Container}.map { it.descriptor.id.value } shouldBe arrayOf("something", "something else")
         listener.testStarted.filter{it.type == TestType.Test}.map { it.descriptor.id.value } shouldBe arrayOf("first")
      }

      test("runLauncher should support second level testpath regex") {
         val listener = SetupLauncherTestListener()
         val result = setupLauncher(
            LauncherArgs(
               "* -- second*",
               "com.sksamuel.kotest.engine.launcher",
               "com.sksamuel.kotest.engine.launcher.DescribeSpec1",
               null, null, null), listener)
         result.isFailure.shouldBeFalse()
         result.getOrNull()?.launch()
         listener.testStarted.filter{it.type == TestType.Container}.map { it.descriptor.id.value } shouldBe arrayOf("something", "something else", "nothing else")
         listener.testStarted.filter{it.type == TestType.Test}.map { it.descriptor.id.value } shouldBe arrayOf("second", "second bar")
      }
   }
}


class SetupLauncherTestListener(
): AbstractTestEngineListener() {
   var specIgnored = mutableMapOf<String, KClass<*>>();
   var specFinished = mutableListOf<String>()
   var testStarted = mutableListOf<TestCase>()
   var testFinished = mutableListOf<String>()
   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
      specIgnored[kclass.qualifiedName?:""] = kclass;
      super.specIgnored(kclass, reason);
   }
   override suspend fun specFinished(kclass: KClass<*>, result: TestResult) {
      kclass.simpleName?.let{
         specFinished.add(it)
      }
      super.specFinished(kclass, result)
   }

   override suspend fun testStarted(testCase: TestCase) {
      testStarted.add(testCase)
   }
   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      testFinished.add(testCase.name.testName)
      super.testFinished(testCase, result)
   }
}



private class DescribeSpec1: DescribeSpec() {
   init {
      describe("something") {
         it("first") {

         }

         it("second") {

         }
      }

      describe("something else") {
         it ("foo") {

         }
      }

      describe("nothing else") {
         it ("bar") {

         }

         it ("second bar") {

         }
      }
   }
}
