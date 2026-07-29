package io.kotest.plugin.intellij.locations

import com.intellij.execution.testframework.sm.runner.SMTestProxy
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class EmbeddedLocationSMTRunnerEventsAdapterTest : LightJavaCodeInsightFixtureTestCase() {

   private fun adapter() = EmbeddedLocationSMTRunnerEventsAdapter(project)

   // -------- Strategy 1: legacy <kotest>...</kotest> displayName tag (older engines) --------

   fun `test should reset presentable path on embedded location on test start`() {
      val proxy = SMTestProxy(
         /* testName = */ "<kotest>io.kotest.Spec.test -- nested</kotest>nested",
         /* isSuite = */ false,
         /* locationUrl = */ "java:suite:io.kotest.Spec/test"
      )
      adapter().onTestStarted(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
      proxy.presentableName shouldBe "nested"
   }

   fun `test should reset presentable path on embedded location on test ignored`() {
      val proxy = SMTestProxy(
         /* testName = */ "<kotest>io.kotest.Spec.test -- nested</kotest>nested",
         /* isSuite = */ false,
         /* locationUrl = */ "java:suite:io.kotest.Spec/test"
      )
      adapter().onTestIgnored(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
      proxy.presentableName shouldBe "nested"
   }

   fun `test should reset presentable path on embedded location on test suite start`() {
      val proxy = SMTestProxy(
         /* testName = */ "<kotest>io.kotest.Spec.test -- nested</kotest>nested",
         /* isSuite = */ false,
         /* locationUrl = */ "java:suite:io.kotest.Spec/test"
      )
      adapter().onSuiteStarted(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
      proxy.presentableName shouldBe "nested"
   }

   // -------- Strategy 2: java:test://<fqn>/<segment>/<segment> URL (current engines) --------

   fun `test should install locator when location url encodes nested path`() {
      val proxy = SMTestProxy(
         /* testName = */ "leaf",
         /* isSuite = */ false,
         /* locationUrl = */ "java:test://io.kotest.Spec/outer/middle/leaf"
      )
      adapter().onTestStarted(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
      // displayName is left untouched - the engine no longer mangles it
      proxy.presentableName shouldBe "leaf"
   }

   fun `test should install locator for suite url`() {
      val proxy = SMTestProxy(
         /* testName = */ "middle",
         /* isSuite = */ true,
         /* locationUrl = */ "java:suite://io.kotest.Spec/outer/middle"
      )
      adapter().onSuiteStarted(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
   }

   fun `test should not install locator for single segment location url on non kotest class`() {
      // Single-segment methodName (no '/' after the FQN) cannot encode a nested Kotest path.
      // No class with this FQN is registered in the fixture project, so it isn't recognised as
      // a Kotest spec, and this looks like a regular JUnit @Test method - leave the default
      // locator in place.
      val proxy = SMTestProxy(
         /* testName = */ "myTest",
         /* isSuite = */ false,
         /* locationUrl = */ "java:test://io.kotest.examples.native.KotlinTest/myTest"
      )
      adapter().onTestStarted(proxy)
      // locator should not be replaced
      (proxy.locator is EmbeddedLocationTestLocator) shouldBe false
   }

   fun `test should install locator for single segment location url on top level kotest test`() {
      // A test directly inside a spec (not nested in a context/describe block) only has one
      // segment in its methodName, but it is still a Kotest test - navigation must not silently
      // fall back to the default locator, which can't resolve a synthetic (non-JVM-method) name.
      myFixture.configureByText(
         "MySpec.kt",
         """
            package com.example

            class MySpec : FunSpec()
         """.trimIndent()
      )
      val proxy = SMTestProxy(
         /* testName = */ "topLevel test",
         /* isSuite = */ false,
         /* locationUrl = */ "java:test://com.example.MySpec/topLevel test"
      )
      adapter().onTestStarted(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
   }

   fun `test should detect java suite classes`() {
      val proxy = SMTestProxy(
         /* testName = */ "a",
         /* isSuite = */ true,
         /* locationUrl = */ "java:suite://io.kotest.Spec"
      )
      adapter().isJavaSuiteClass(proxy) shouldBe true
   }

   fun `test should skip java suites that are not classes`() {
      val proxy = SMTestProxy(
         /* testName = */ "a",
         /* isSuite = */ true,
         /* locationUrl = */ "java:suite://io.kotest.examples.native.KotlinTest/nested"
      )
      adapter().isJavaSuiteClass(proxy) shouldBe false
   }

   fun `test should skip java tests`() {
      val proxy = SMTestProxy(
         /* testName = */ "a",
         /* isSuite = */ false,
         /* locationUrl = */ "java:test://io.kotest.examples.native.KotlinTest/myTest"
      )
      adapter().isJavaSuiteClass(proxy) shouldBe false
   }

   // -------- Parser tests for parseLocationUrl --------

   fun `test parseLocationUrl java test nested`() {
      EmbeddedLocationParser.parseLocationUrl(
         "java:test://com.example.MySpec/outer/inner/leaf",
         "leaf"
      ) { false } shouldBe EmbeddedLocation("com.example.MySpec/outer -- inner -- leaf", "leaf")
   }

   fun `test parseLocationUrl java suite nested`() {
      EmbeddedLocationParser.parseLocationUrl(
         "java:suite://com.example.MySpec/outer/inner",
         "inner"
      ) { false } shouldBe EmbeddedLocation("com.example.MySpec/outer -- inner", "inner")
   }

   fun `test parseLocationUrl single segment non kotest class returns null`() {
      EmbeddedLocationParser.parseLocationUrl(
         "java:test://com.example.MySpec/topLevel",
         "topLevel"
      ) { false }.shouldBeNull()
   }

   fun `test parseLocationUrl single segment kotest class returns location`() {
      EmbeddedLocationParser.parseLocationUrl(
         "java:test://com.example.MySpec/topLevel",
         "topLevel"
      ) { true } shouldBe EmbeddedLocation("com.example.MySpec/topLevel", "topLevel")
   }

   fun `test parseLocationUrl unknown protocol returns null`() {
      EmbeddedLocationParser.parseLocationUrl(
         "kotest://com.example.MySpec/foo/bar",
         "bar"
      ) { false }.shouldBeNull()
   }

   fun `test parseLocationUrl null returns null`() {
      EmbeddedLocationParser.parseLocationUrl(null, "anything") { false }.shouldBeNull()
   }

   // -------- ancestorNames: restoring the path Gradle test events drop --------

   fun `test parseLocationUrl uses ancestorNames to restore a nested path for a single segment url`() {
      // IntelliJ's Gradle test event integration collapses "base/inner root/child1" down to just
      // "child1" on the locationUrl - ancestorNames (from GradleParentIdParser) restores it.
      EmbeddedLocationParser.parseLocationUrl(
         "java:test://com.example.MySpec/child1",
         "child1",
         listOf("base", "inner root")
      ) { true } shouldBe EmbeddedLocation("com.example.MySpec/base -- inner root -- child1", "child1")
   }

   fun `test parseLocationUrl ignores ancestorNames when the url already encodes a nested path`() {
      // A multi-segment MethodSource-derived url (from the IntelliJ-native JUnit launcher) already
      // has the full path - ancestorNames must not be appended on top of it.
      EmbeddedLocationParser.parseLocationUrl(
         "java:test://com.example.MySpec/outer/inner/leaf",
         "leaf",
         listOf("shouldNotAppear")
      ) { false } shouldBe EmbeddedLocation("com.example.MySpec/outer -- inner -- leaf", "leaf")
   }

   fun `test parseLocationUrl with empty ancestorNames behaves as a top level test`() {
      EmbeddedLocationParser.parseLocationUrl(
         "java:test://com.example.MySpec/topLevel",
         "topLevel",
         emptyList()
      ) { true } shouldBe EmbeddedLocation("com.example.MySpec/topLevel", "topLevel")
   }

   // -------- GradleParentIdParser --------

   fun `test ancestorContextNames extracts and reverses suite names ignoring the leading id`() {
      // real strings captured from a debugger session against GradleSMTestProxy#getParentId
      val parentId =
         "[-1862748639:723354132] > [Test suite 'inner root'] > [Test suite 'base'] > " +
            "[Test class io.github.alfonsoristorato.jpaspeckotlindsl.archunit.Random] > [Task :jpa-spec-kotlin-dsl:test]"
      GradleParentIdParser.ancestorContextNames(parentId) shouldBe listOf("base", "inner root")
   }

   fun `test ancestorContextNames for a container one level below the spec`() {
      val parentId =
         "[-1862748639:723354132] > [Test suite 'base'] > " +
            "[Test class io.github.alfonsoristorato.jpaspeckotlindsl.archunit.Random] > [Task :jpa-spec-kotlin-dsl:test]"
      GradleParentIdParser.ancestorContextNames(parentId) shouldBe listOf("base")
   }

   fun `test ancestorContextNames for a top level container directly under the spec`() {
      val parentId =
         "[-1862748639:723354132] > [Test class io.github.alfonsoristorato.jpaspeckotlindsl.archunit.Random] > [Task :jpa-spec-kotlin-dsl:test]"
      GradleParentIdParser.ancestorContextNames(parentId) shouldBe emptyList()
   }

   fun `test ancestorContextNames disambiguates a second root with the same inner container name`() {
      val parentId =
         "[-1862748639:723354132] > [Test suite 'inner root'] > [Test suite 'base 2'] > " +
            "[Test class io.github.alfonsoristorato.jpaspeckotlindsl.archunit.Random] > [Task :jpa-spec-kotlin-dsl:test]"
      GradleParentIdParser.ancestorContextNames(parentId) shouldBe listOf("base 2", "inner root")
   }

   fun `test ancestorContextNames returns empty list for null parentId`() {
      GradleParentIdParser.ancestorContextNames(null) shouldBe emptyList()
   }
}
