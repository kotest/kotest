package com.sksamuel.kotest.runner.junit5

import io.kotest.core.spec.style.FunSpec
import io.kotest.framework.discovery.Discovery
import io.kotest.framework.discovery.DiscoveryFilter
import io.kotest.framework.discovery.DiscoveryRequest
import io.kotest.framework.discovery.DiscoverySelector
import io.kotest.framework.discovery.Modifier
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.ClassNameFilter
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.engine.discovery.PackageNameFilter
import org.junit.platform.launcher.EngineFilter.excludeEngines
import org.junit.platform.launcher.EngineFilter.includeEngines
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder

class DiscoveryTest : FunSpec({

   test("kotest should return Nil if request excludes kotest engine") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            excludeEngines(KotestJunitPlatformTestEngine.EngineId)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 0
   }

   test("kotest should return classes if request includes kotest engine") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            includeEngines(KotestJunitPlatformTestEngine.EngineId)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 23
   }

   test("kotest should return classes if request has no included or excluded test engines") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            PackageNameFilter.includePackageNames("com.sksamuel.kotest")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 23
   }

   test("kotest should support include package name filter") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            PackageNameFilter.includePackageNames("com.sksamuel.kotest.runner.junit5.mypackage")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.map { it.qualifiedName } shouldBe listOf(
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
            ClassNameFilter.includeClassNamePatterns(DiscoveryTest::class.java.canonicalName)
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.map { it.qualifiedName } shouldBe listOf(DiscoveryTest::class.java.canonicalName)
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
            ClassNameFilter.includeClassNamePatterns(".*DiscoveryTest")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.size shouldBe 1
   }

   test("kotest should recognize suffixed class name pattern filters") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .filters(
            ClassNameFilter.includeClassNamePatterns("com.sksamuel.kotest.runner.junit5.DiscoveryTe.*")
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
      descriptor.classes.map { it.qualifiedName } shouldBe listOf(
         com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec1::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.DummySpec2::class.java.canonicalName,
         com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage.DummySpec2::class.java.canonicalName,
      )
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
   }

   test("package selector should include packages and subpackages") {
      val req = LauncherDiscoveryRequestBuilder.request()
         .selectors(
            DiscoverySelectors.selectPackage("com.sksamuel.kotest.runner.junit5.mypackage")
         )
         .build()
      val engine = KotestJunitPlatformTestEngine()
      val descriptor = engine.discover(req, UniqueId.forEngine("testengine"))
      descriptor.classes.map { it.qualifiedName } shouldBe listOf(
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
      Discovery().discover(
         DiscoveryRequest(
            selectors = listOf(DiscoverySelector.PackageDiscoverySelector("com.sksamuel.kotest.runner.junit5.mypackage3")),
            filters = listOf(DiscoveryFilter.ClassModifierDiscoveryFilter(setOf(Modifier.Public)))
         )
      ).specs.map { it.simpleName }.toSet() shouldBe setOf("DummySpec7")
   }

   test("kotest should detect internal spec classes when internal flag is set") {
      Discovery().discover(
         DiscoveryRequest(
            selectors = listOf(DiscoverySelector.PackageDiscoverySelector("com.sksamuel.kotest.runner.junit5.mypackage3")),
            filters = listOf(DiscoveryFilter.ClassModifierDiscoveryFilter(setOf(Modifier.Public, Modifier.Internal)))
         )
      ).specs.map { it.simpleName }.toSet() shouldBe setOf("DummySpec6", "DummySpec7")
   }

   test("kotest should detect only internal specs if public is not set") {
      Discovery().discover(
         DiscoveryRequest(
            selectors = listOf(DiscoverySelector.PackageDiscoverySelector("com.sksamuel.kotest.runner.junit5.mypackage3")),
            filters = listOf(DiscoveryFilter.ClassModifierDiscoveryFilter(setOf(Modifier.Internal)))
         )
      ).specs.map { it.simpleName }.toSet() shouldBe setOf("DummySpec6")
   }
})
