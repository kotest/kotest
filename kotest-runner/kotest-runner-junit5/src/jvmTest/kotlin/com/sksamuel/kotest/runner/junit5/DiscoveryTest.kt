package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.KotestEngineProperties
import io.kotest.framework.discovery.Discovery
import io.kotest.framework.discovery.DiscoveryFilter
import io.kotest.framework.discovery.DiscoveryRequest
import io.kotest.framework.discovery.DiscoverySelector
import io.kotest.framework.discovery.Modifier
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import io.kotest.runner.junit.platform.Segment
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassNameFilter
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.engine.discovery.PackageNameFilter
import org.junit.platform.engine.support.descriptor.ClassSource
import org.junit.platform.launcher.EngineFilter.excludeEngines
import org.junit.platform.launcher.EngineFilter.includeEngines
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder

@Isolate
@EnabledIf(LinuxCondition::class)
class DiscoveryTestWithoutSelectors : FunSpec({
   aroundTest { (testCase, execute) ->
      check(System.setProperty(KotestEngineProperties.discoveryClasspathFallbackEnabled, "true") == null)
      val result = execute(testCase)
      System.clearProperty(KotestEngineProperties.discoveryClasspathFallbackEnabled)
      result
   }

   test("kotest should return Nil if request excludes kotest engine") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            excludeEngines(KotestJunitPlatformTestEngine.ENGINE_ID)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 0
   }

   test("kotest should return classes if request includes kotest engine") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            includeEngines(KotestJunitPlatformTestEngine.ENGINE_ID)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 30
   }

   test("kotest should return classes if request has no included or excluded test engines") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            PackageNameFilter.includePackageNames("com.sksamuel.kotest")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 28
   }

   test("kotest should support include package name filter") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            PackageNameFilter.includePackageNames("com.sksamuel.kotest.runner.junit5.mypackage")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.map { it.qualifiedName }.toSet() shouldBe setOf(
         "com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1",
         "com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec1",
         "com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2",
         "com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec2",
      )
   }

   test("kotest should return Nil if include package name filters matches nothing") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            PackageNameFilter.includePackageNames("com.sksamuel.kotest.runner.foobar")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 0
   }

   test("kotest should recognize fully qualified include class name filters") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            ClassNameFilter.includeClassNamePatterns(DiscoveryTestWithoutSelectors::class.java.canonicalName)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.map { it.qualifiedName } shouldBe
         listOf(DiscoveryTestWithoutSelectors::class.java.canonicalName)
   }

   test("kotest should return Nil if include class name filters have no matching values") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            ClassNameFilter.includeClassNamePatterns("Foo")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 0
   }

   test("kotest should recognize prefixed class name filters") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            ClassNameFilter.includeClassNamePatterns(".*DiscoveryTestWithoutSelectors")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 1
   }

   test("kotest should recognize suffixed class name pattern filters") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            ClassNameFilter.includeClassNamePatterns("com.sksamuel.kotest.runner.junit5.DiscoveryTestWithout.*")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 1
   }

   test("kotest should support excluded class name pattern filters") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            PackageNameFilter.includePackageNames("com.sksamuel.kotest.runner.junit5.mypackage"),
            ClassNameFilter.excludeClassNamePatterns(".*2")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.map { it.qualifiedName } shouldBe listOf(
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec1::class.java.canonicalName,
      )
   }

   test("kotest should support excluded fully qualified class name") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            PackageNameFilter.includePackageNames("com.sksamuel.kotest.runner.junit5.mypackage"),
            ClassNameFilter.excludeClassNamePatterns(com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.canonicalName)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.map { it.qualifiedName }.toSet() shouldBe setOf(
         com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec1::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec2::class.java.canonicalName,
      )
   }

   test("kotest should discover nothing if request contains no selectors") {
      Discovery(configuration = ProjectConfiguration()).discover(
         DiscoveryRequest()
      ).specs.map { it.simpleName }.shouldBeEmpty()
   }

   test("kotest should discover specs if request contains no selectors but discoveryClasspathScanningEnabled = true") {
      Discovery(configuration = ProjectConfiguration().apply { discoveryClasspathFallbackEnabled = true }).discover(
         DiscoveryRequest()
      ).specs.map { it.simpleName }.shouldNotBeEmpty()
   }
})

@Isolate
@EnabledIf(LinuxCondition::class)
class DiscoveryTestWithSelectors : FunSpec({
   test("kotest should return Nil for uniqueId selectors if request excludes kotest engine") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectUniqueId(
               UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID)
                  .append(
                     Segment.Spec.value,
                     com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.qualifiedName
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
                     com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.qualifiedName
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
         .selectors(DiscoverySelectors.selectUniqueId(engineId.append(Segment.Spec.value, testClass.qualifiedName)))
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
      descriptor.classes.map { it.qualifiedName } shouldBe listOf(com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java.canonicalName)
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
      descriptor.classes.map { it.qualifiedName } shouldBe listOf(
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java.canonicalName,
      )
      descriptor.children.map { (it.source.get() as ClassSource).javaClass } shouldBe listOf(
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java,
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java,
      )
   }

   test("package selector should include packages and subpackages") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectPackage("com.sksamuel.kotest.runner.junit5.mypackage")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.map { it.qualifiedName }.toSet() shouldBe setOf(
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec1::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec2::class.java.canonicalName,
      )
   }

   test("discovery should support multiple package selectors") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectPackage("com.sksamuel.kotest.runner.junit5.mypackage2"),
            DiscoverySelectors.selectPackage("com.sksamuel.kotest.runner.junit5.mypackage3")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.map { it.qualifiedName } shouldBe listOf(
         com.sksamuel.kotest.runner.junit5.mypackage2.DummySpec3::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage2.DummySpec4::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage3.DummySpec6::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage3.DummySpec7::class.java.canonicalName,
      )
   }

   test("kotest should support mixed package and class selectors") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectClass("com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1"),
            DiscoverySelectors.selectPackage("com.sksamuel.kotest.runner.junit5.mypackage2")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.map { it.qualifiedName } shouldBe listOf(
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage2.DummySpec3::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage2.DummySpec4::class.java.canonicalName
      )
   }

   test("kotest should detect only public spec classes when internal flag is not set") {
      Discovery(configuration = ProjectConfiguration()).discover(
         DiscoveryRequest(
            selectors = listOf(DiscoverySelector.PackageDiscoverySelector("com.sksamuel.kotest.runner.junit5.mypackage3")),
            filters = listOf(DiscoveryFilter.ClassModifierDiscoveryFilter(setOf(Modifier.Public)))
         )
      ).specs.map { it.simpleName }.toSet() shouldBe setOf("DummySpec7")
   }

   test("kotest should detect internal spec classes when internal flag is set") {
      Discovery(configuration = ProjectConfiguration()).discover(
         DiscoveryRequest(
            selectors = listOf(DiscoverySelector.PackageDiscoverySelector("com.sksamuel.kotest.runner.junit5.mypackage3")),
            filters = listOf(DiscoveryFilter.ClassModifierDiscoveryFilter(setOf(Modifier.Public, Modifier.Internal)))
         )
      ).specs.map { it.simpleName }.toSet() shouldBe setOf("DummySpec6", "DummySpec7")
   }

   test("kotest should detect only internal specs if public is not set") {
      Discovery(configuration = ProjectConfiguration()).discover(
         DiscoveryRequest(
            selectors = listOf(DiscoverySelector.PackageDiscoverySelector("com.sksamuel.kotest.runner.junit5.mypackage3")),
            filters = listOf(DiscoveryFilter.ClassModifierDiscoveryFilter(setOf(Modifier.Internal)))
         )
      ).specs.map { it.simpleName }.toSet() shouldBe setOf("DummySpec6")
   }
})
