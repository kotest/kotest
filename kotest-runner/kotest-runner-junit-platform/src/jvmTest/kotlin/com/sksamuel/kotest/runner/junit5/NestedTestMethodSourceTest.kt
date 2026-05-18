package com.sksamuel.kotest.runner.junit5

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.engine.support.descriptor.MethodSource
import org.junit.platform.testkit.engine.EngineTestKit

/**
 * The IntelliJ plugin relies on the [MethodSource] attached to each leaf test descriptor
 * being of the form (className=<fqn>, methodName=<segment>/<segment>/...). The IDEA JUnit5
 * launcher converts that source into a `java:test://<fqn>/<segment>/<segment>` `locationHint`
 * which the plugin parses on `proxy.locationUrl` to drive jump-to-source navigation without
 * mangling the displayName.
 *
 * This test pins that contract end-to-end: it runs a Kotest spec with deeply nested tests
 * through the JUnit Platform [EngineTestKit] (the same Launcher pipeline IntelliJ uses) and
 * asserts that every leaf test has a [MethodSource] with className equal to the spec FQN and
 * methodName equal to the path of nested-test names joined with `/`.
 *
 * If JUnit Platform ever changes how it carries [org.junit.platform.engine.TestSource] for
 * dynamic tests, this test will fail and we will need to revisit the navigation strategy.
 */
class NestedTestMethodSourceTest : FunSpec({

   val fqn = NestedSample::class.java.name

   test("leaf tests should have MethodSource(fqn, segments joined by '/')") {
      val descriptors = runEngine().allEvents().dynamicallyRegistered().list()
         .map { it.testDescriptor }
         .filter { it.isTest }

      val pairs = descriptors.map { d ->
         withClue("leaf '${d.displayName}' should use MethodSource") {
            d.source.orElseThrow().shouldBeInstanceOf<MethodSource>()
         }
         val ms = d.source.get() as MethodSource
         ms.className to ms.methodName
      }

      pairs shouldContainExactlyInAnyOrder listOf(
         fqn to "leaf at root",
         fqn to "outer/inner leaf",
         fqn to "outer/middle/deep leaf",
         fqn to "siblings/with slashes/in name",
      )
   }

   test("container descriptors should use ClassSource so Android Studio renders the tree correctly") {
      val descriptors = runEngine().allEvents().dynamicallyRegistered().list()
         .map { it.testDescriptor }
         .filter { it.isContainer }

      descriptors.forEach { d ->
         withClue("container '${d.displayName}' should use ClassSource") {
            val source = d.source.orElseThrow().shouldBeInstanceOf<ClassSource>()
            source.className shouldBe fqn
         }
      }
   }
})

private fun runEngine() = EngineTestKit
   .engine(KotestJunitPlatformTestEngine.ENGINE_ID)
   .selectors(selectClass(NestedSample::class.java))
   .configurationParameter("allow_private", "true")
   .execute()

private class NestedSample : FunSpec({

   test("leaf at root") {}

   context("outer") {
      test("inner leaf") {}
      context("middle") {
         test("deep leaf") {}
      }
   }

   context("siblings") {
      context("with slashes") {
         test("in name") {}
      }
   }
})
