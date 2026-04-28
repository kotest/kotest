package io.kotest.plugin.intellij.locations

import com.intellij.execution.testframework.sm.runner.SMTestProxy
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.Test

class EmbeddedLocationSMTRunnerEventsAdapterTest {

   // -------- Strategy 1: legacy <kotest>...</kotest> displayName tag (older engines) --------

   @Test
   fun shouldResetPresentablePathOnEmbeddedLocationOnTestStart() {
      val proxy = SMTestProxy(
         /* testName = */ "<kotest>io.kotest.Spec.test -- nested</kotest>nested",
         /* isSuite = */ false,
         /* locationUrl = */ "java:suite:io.kotest.Spec/test"
      )
      EmbeddedLocationSMTRunnerEventsAdapter().onTestStarted(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
      proxy.presentableName shouldBe "nested"
   }


   @Test
   fun shouldResetPresentablePathOnEmbeddedLocationOnTestIgnored() {
      val proxy = SMTestProxy(
         /* testName = */ "<kotest>io.kotest.Spec.test -- nested</kotest>nested",
         /* isSuite = */ false,
         /* locationUrl = */ "java:suite:io.kotest.Spec/test"
      )
      EmbeddedLocationSMTRunnerEventsAdapter().onTestIgnored(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
      proxy.presentableName shouldBe "nested"
   }

   @Test
   fun shouldResetPresentablePathOnEmbeddedLocationOnTestSuiteStart() {
      val proxy = SMTestProxy(
         /* testName = */ "<kotest>io.kotest.Spec.test -- nested</kotest>nested",
         /* isSuite = */ false,
         /* locationUrl = */ "java:suite:io.kotest.Spec/test"
      )
      EmbeddedLocationSMTRunnerEventsAdapter().onSuiteStarted(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
      proxy.presentableName shouldBe "nested"
   }

   // -------- Strategy 2: java:test://<fqn>/<segment>/<segment> URL (current engines) --------

   @Test
   fun shouldInstallLocatorWhenLocationUrlEncodesNestedPath() {
      val proxy = SMTestProxy(
         /* testName = */ "leaf",
         /* isSuite = */ false,
         /* locationUrl = */ "java:test://io.kotest.Spec/outer/middle/leaf"
      )
      EmbeddedLocationSMTRunnerEventsAdapter().onTestStarted(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
      // displayName is left untouched - the engine no longer mangles it
      proxy.presentableName shouldBe "leaf"
   }

   @Test
   fun shouldInstallLocatorForSuiteUrl() {
      val proxy = SMTestProxy(
         /* testName = */ "middle",
         /* isSuite = */ true,
         /* locationUrl = */ "java:suite://io.kotest.Spec/outer/middle"
      )
      EmbeddedLocationSMTRunnerEventsAdapter().onSuiteStarted(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
   }

   @Test
   fun shouldNotInstallLocatorForSingleSegmentLocationUrl() {
      // Single-segment methodName (no '/' after the FQN) cannot encode a nested Kotest path,
      // and could be a regular JUnit @Test method - leave the default locator in place.
      val proxy = SMTestProxy(
         /* testName = */ "myTest",
         /* isSuite = */ false,
         /* locationUrl = */ "java:test://io.kotest.examples.native.KotlinTest/myTest"
      )
      EmbeddedLocationSMTRunnerEventsAdapter().onTestStarted(proxy)
      // locator should not be replaced
      (proxy.locator is EmbeddedLocationTestLocator) shouldBe false
   }

   @Test
   fun shouldDetectJavaSuiteClasses() {
      val proxy = SMTestProxy(
         /* testName = */ "a",
         /* isSuite = */ true,
         /* locationUrl = */ "java:suite://io.kotest.Spec"
      )
      EmbeddedLocationSMTRunnerEventsAdapter().isJavaSuiteClass(proxy) shouldBe true
   }

   @Test
   fun shouldSkipJavaSuitesThatAreNotClasses() {
      val proxy = SMTestProxy(
         /* testName = */ "a",
         /* isSuite = */ true,
         /* locationUrl = */ "java:suite://io.kotest.examples.native.KotlinTest/nested"
      )
      EmbeddedLocationSMTRunnerEventsAdapter().isJavaSuiteClass(proxy) shouldBe false
   }

   @Test
   fun shouldSkipJavaTests() {
      val proxy = SMTestProxy(
         /* testName = */ "a",
         /* isSuite = */ false,
         /* locationUrl = */ "java:test://io.kotest.examples.native.KotlinTest/myTest"
      )
      EmbeddedLocationSMTRunnerEventsAdapter().isJavaSuiteClass(proxy) shouldBe false
   }

   // -------- Parser tests for parseLocationUrl --------

   @Test
   fun parseLocationUrl_javaTestNested() {
      EmbeddedLocationParser.parseLocationUrl(
         "java:test://com.example.MySpec/outer/inner/leaf",
         "leaf"
      ) shouldBe EmbeddedLocation("com.example.MySpec/outer -- inner -- leaf", "leaf")
   }

   @Test
   fun parseLocationUrl_javaSuiteNested() {
      EmbeddedLocationParser.parseLocationUrl(
         "java:suite://com.example.MySpec/outer/inner",
         "inner"
      ) shouldBe EmbeddedLocation("com.example.MySpec/outer -- inner", "inner")
   }

   @Test
   fun parseLocationUrl_singleSegment_returnsNull() {
      EmbeddedLocationParser.parseLocationUrl(
         "java:test://com.example.MySpec/topLevel",
         "topLevel"
      ).shouldBeNull()
   }

   @Test
   fun parseLocationUrl_unknownProtocol_returnsNull() {
      EmbeddedLocationParser.parseLocationUrl(
         "kotest://com.example.MySpec/foo/bar",
         "bar"
      ).shouldBeNull()
   }

   @Test
   fun parseLocationUrl_null_returnsNull() {
      EmbeddedLocationParser.parseLocationUrl(null, "anything").shouldBeNull()
   }
}
