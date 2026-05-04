package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import io.kotest.runner.junit.platform.Segment
import io.kotest.runner.junit.platform.discovery.Discovery
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.launcher.EngineFilter.excludeEngines
import org.junit.platform.launcher.EngineFilter.includeEngines
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder

@Isolate
@EnabledIf(LinuxOnlyGithubCondition::class)
class DiscoveryTestWithSelectors : FunSpec({
   test("kotest should return Nil for uniqueId selectors if request excludes kotest engine") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectUniqueId(
               UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID)
                  .append(
                     Segment.Spec.value,
                     com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.name
                  )
            )
         )
         .filters(
            excludeEngines(KotestJunitPlatformTestEngine.ENGINE_ID)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.children.size shouldBe 0
   }

   test("kotest should return Nil for uniqueId selectors on non kotest engine") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectUniqueId(
               UniqueId.forEngine("failgood")
                  .append(
                     Segment.Spec.value,
                     com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.name
                  )
            )
         )
         .filters(
            includeEngines(KotestJunitPlatformTestEngine.ENGINE_ID)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID))
      descriptor.children.size shouldBe 0
   }

   test("kotest should return Nil for uniqueId selectors on non existing class") {
      val engineId = UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID)
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(DiscoverySelectors.selectUniqueId(engineId.append(Segment.Spec.value, "whatever")))
         .filters(
            includeEngines(KotestJunitPlatformTestEngine.ENGINE_ID)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, engineId)
      descriptor.children.size shouldBe 0
   }

   test("kotest should return class for uniqueId selectors") {
      val engineId = UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID)
      val testClass = com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(DiscoverySelectors.selectUniqueId(engineId.append(Segment.Spec.value, testClass.java.name)))
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, engineId)
      descriptor.children.size shouldBe 1
      val firstChild = descriptor.children.first()
      (firstChild.source.get() as ClassSource).javaClass shouldBe testClass.java
   }

   test("kotest should support selected class names") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectClass("com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.specs.map { it.fqn } shouldBe listOf(com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java.canonicalName)
      descriptor.children.map { (it.source.get() as ClassSource).javaClass } shouldBe listOf(com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java)
   }

   test("kotest should support multiple selected class names") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectClass("com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1"),
            DiscoverySelectors.selectClass("com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.specs.map { it.fqn } shouldBe listOf(
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java.canonicalName,
      )
      descriptor.children.map { (it.source.get() as ClassSource).javaClass } shouldBe listOf(
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java,
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java,
      )
   }

   test("classpath root selector should discover spec classes in that root") {
      val classpathRoot = java.nio.file.Paths.get(
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java
            .protectionDomain.codeSource.location.toURI()
      )
      val engineId = UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID)
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(DiscoverySelectors.selectClasspathRoots(setOf(classpathRoot)))
         .build()
      val result = Discovery.discover(engineId, req)
      result.specs.map { it.fqn } shouldContain com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.canonicalName
      result.specs.map { it.fqn } shouldContain com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java.canonicalName
   }

   test("engine should not skip discovery for classpath root selectors") {
      val classpathRoot = java.nio.file.Paths.get(
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java
            .protectionDomain.codeSource.location.toURI()
      )
      val engineId = UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID)
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(DiscoverySelectors.selectClasspathRoots(setOf(classpathRoot)))
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, engineId)
      descriptor.specs.map { it.fqn } shouldContain com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.canonicalName
      descriptor.specs.map { it.fqn } shouldContain com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java.canonicalName
   }

   // Regression test for https://github.com/kotest/kotest/issues/5773
   // AGP 9+ passes MethodSelectors alongside ClasspathRootSelectors for pre-discovered @Test methods.
   // Kotest must not bail out when MethodSelectors are present if valid classpath/class selectors are also present.
   test("engine should not skip discovery when method selectors are present alongside classpath root selectors") {
      val classpathRoot = java.nio.file.Paths.get(
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java
            .protectionDomain.codeSource.location.toURI()
      )
      val engineId = UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID)
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectClasspathRoots(setOf(classpathRoot)) +
               listOf(DiscoverySelectors.selectMethod("com.example.SomeJunitTest#someMethod"))
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, engineId)
      descriptor.specs.map { it.fqn } shouldContain com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.canonicalName
      descriptor.specs.map { it.fqn } shouldContain com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java.canonicalName
   }

   // Regression test for #5981: gradle test-retry / IntelliJ "Re-run Failed Tests"
   // commonly send both a ClassSelector for the spec and a UniqueIdSelector for a test
   // inside the same spec. Discovery used to return the spec twice, causing it to be
   // instantiated and executed twice and producing duplicate executionStarted events.
   test("discovery should return a spec only once when both ClassSelector and UniqueIdSelector target it") {
      val engineId = UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID)
      val testClass = com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectClass(testClass.java.name),
            DiscoverySelectors.selectUniqueId(engineId.append(Segment.Spec.value, testClass.java.name)),
         )
         .build()
      val result = Discovery.discover(engineId, req)
      result.specs.map { it.fqn } shouldBe listOf(testClass.java.canonicalName)
   }

   xtest("package selector should include packages and subpackages") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectPackage("com.sksamuel.kotest.runner.junit5.mypackage")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.specs.map { it.fqn }.toSet() shouldBe setOf(
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec1::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec2::class.java.canonicalName,
      )
   }

   xtest("discovery should support multiple package selectors") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectPackage("com.sksamuel.kotest.runner.junit5.mypackage2"),
            DiscoverySelectors.selectPackage("com.sksamuel.kotest.runner.junit5.mypackage3")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.specs.map { it.fqn } shouldBe listOf(
         com.sksamuel.kotest.runner.junit5.mypackage2.DummySpec3::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage2.DummySpec4::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage3.DummySpec6::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage3.DummySpec7::class.java.canonicalName,
      )
   }

   xtest("kotest should support mixed package and class selectors") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectClass("com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1"),
            DiscoverySelectors.selectPackage("com.sksamuel.kotest.runner.junit5.mypackage2")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.specs.map { it.fqn } shouldBe listOf(
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage2.DummySpec3::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage2.DummySpec4::class.java.canonicalName
      )
   }

//   test("kotest should detect only public spec classes when internal flag is not set") {
//      Discovery(nestedJarScanning, externalClasses, ignoreClassVisibility, classgraph()).discover(
//         DiscoveryRequest(
//            selectors = listOf(DiscoverySelector.PackageDiscoverySelector("com.sksamuel.kotest.runner.junit5.mypackage3")),
//            filters = listOf(DiscoveryFilter.ClassModifierDiscoveryFilter(setOf(Modifier.Public)))
//         )
//      ).specs.map { it.simpleName }.toSet() shouldBe setOf("DummySpec7")
//   }
//
//   test("kotest should detect internal spec classes when internal flag is set") {
//      Discovery(nestedJarScanning, externalClasses, ignoreClassVisibility, classgraph()).discover(
//         DiscoveryRequest(
//            selectors = listOf(DiscoverySelector.PackageDiscoverySelector("com.sksamuel.kotest.runner.junit5.mypackage3")),
//            filters = listOf(DiscoveryFilter.ClassModifierDiscoveryFilter(setOf(Modifier.Public, Modifier.Internal)))
//         )
//      ).specs.map { it.simpleName }.toSet() shouldBe setOf("DummySpec6", "DummySpec7")
//   }
//
//   test("kotest should detect only internal specs if public is not set") {
//      Discovery(nestedJarScanning, externalClasses, ignoreClassVisibility, classgraph()).discover(
//         DiscoveryRequest(
//            selectors = listOf(DiscoverySelector.PackageDiscoverySelector("com.sksamuel.kotest.runner.junit5.mypackage3")),
//            filters = listOf(DiscoveryFilter.ClassModifierDiscoveryFilter(setOf(Modifier.Internal)))
//         )
//      ).specs.map { it.simpleName }.toSet() shouldBe setOf("DummySpec6")
//   }
})
