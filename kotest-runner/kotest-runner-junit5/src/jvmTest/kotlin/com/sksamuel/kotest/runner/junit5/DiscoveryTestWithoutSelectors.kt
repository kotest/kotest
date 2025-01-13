package com.sksamuel.kotest.runner.junit5

import com.sksamuel.kotest.runner.junit5.mypackage.DummySpec1
import com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.KotestEngineProperties
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassNameFilter
import org.junit.platform.engine.discovery.PackageNameFilter
import org.junit.platform.launcher.EngineFilter
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

   test("discovery should be skipped if request excludes kotest engine") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            EngineFilter.excludeEngines(KotestJunitPlatformTestEngine.Companion.ENGINE_ID)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 0
   }

   // will be replaced when we add the discovery builders
   xtest("discovery should run if request includes kotest engine") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            EngineFilter.includeEngines(KotestJunitPlatformTestEngine.Companion.ENGINE_ID)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 30
   }

   // will be replaced when we add the discovery builders
   xtest("kotest should return classes if request has no included or excluded test engines") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            PackageNameFilter.includePackageNames("com.sksamuel.kotest")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 28
   }

   xtest("kotest should support include package name filter") {
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

   xtest("kotest should recognize fully qualified include class name filters") {
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

   xtest("kotest should recognize prefixed class name filters") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            ClassNameFilter.includeClassNamePatterns(".*DiscoveryTestWithoutSelectors")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 1
   }

   xtest("kotest should recognize suffixed class name pattern filters") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            ClassNameFilter.includeClassNamePatterns("com.sksamuel.kotest.runner.junit5.DiscoveryTestWithout.*")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 1
   }

   xtest("kotest should support excluded class name pattern filters") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            PackageNameFilter.includePackageNames("com.sksamuel.kotest.runner.junit5.mypackage"),
            ClassNameFilter.excludeClassNamePatterns(".*2")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.map { it.qualifiedName } shouldBe listOf(
         DummySpec1::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec1::class.java.canonicalName,
      )
   }

   xtest("kotest should support excluded fully qualified class name") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            PackageNameFilter.includePackageNames("com.sksamuel.kotest.runner.junit5.mypackage"),
            ClassNameFilter.excludeClassNamePatterns(DummySpec1::class.java.canonicalName)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.map { it.qualifiedName }.toSet() shouldBe setOf(
         com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec1::class.java.canonicalName,
         DummySpec2::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec2::class.java.canonicalName,
      )
   }

//   test("kotest should discover nothing if request contains no selectors") {
//      Discovery(nestedJarScanning, externalClasses, ignoreClassVisibility, classgraph()).discover(
//         DiscoveryRequest()
//      ).specs.map { it.simpleName }.shouldBeEmpty()
//   }

   xtest("kotest should discover specs if request contains no selectors but discoveryClasspathScanningEnabled = true") {
//      Discovery(configuration = ProjectConfiguration().apply { discoveryClasspathFallbackEnabled = true }).discover(
//         DiscoveryRequest()
//      ).specs.map { it.simpleName }.shouldNotBeEmpty()
   }
})
