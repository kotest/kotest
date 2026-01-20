package com.sksamuel.kotest.engine.spec.execution

import io.kotest.common.Platform
import io.kotest.core.annotation.AlwaysFalseCondition
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.SpecRefExtension
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.IgnoredSpecListener
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.TestEngineContext
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.spec.execution.SpecRefExecutor
import io.kotest.engine.test.TestResult
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

class SpecRefExecutorTest : FunSpec() {
   init {

      test("SpecRefExecutor should fire IgnoredSpecListener when a spec is disabled") {

         var fired = false
         val ext = object : IgnoredSpecListener {
            override suspend fun ignoredSpec(kclass: KClass<*>, reason: String?) {
               fired = true
            }
         }

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ext)
         }
         val e = SpecRefExecutor(TestEngineContext(c, Platform.JVM))
         e.execute(SpecRef.Reference(AlwaysFalseSpec::class))
         fired.shouldBeTrue()
      }

      test("SpecRefExecutor should notify listener when a spec is disabled with reason") {
         var r: String? = null
         val listener = object : AbstractTestEngineListener() {
            override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
               r = reason
            }
         }
         val c = object : AbstractProjectConfig() {
         }
         val e = SpecRefExecutor(TestEngineContext(c, Platform.JVM).withListener(listener))
         e.execute(SpecRef.Reference(AlwaysFalseSpec::class))
         r shouldBe "Disabled by @EnabledIf (AlwaysFalseCondition)"
      }

      test("SpecRefExecutor should invoke SpecRefExtensions") {
         var breadcrumb = ""
         val ext = SpecRefExtension { _, process ->
            breadcrumb += "a"
            process.invoke()
         }
         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ext)
         }
         val e = SpecRefExecutor(TestEngineContext(c, Platform.JVM))
         e.execute(SpecRef.Reference(BasicSpec::class))
         breadcrumb shouldBe "a"
      }

      test("SpecRefExecutor should invoke PrepareSpecListener and FinalizeSpecListener") {
         var breadcrumb = ""
         val prepareSpec = object : PrepareSpecListener {
            override suspend fun prepareSpec(kclass: KClass<out Spec>) {
               breadcrumb += "a"
            }
         }
         val finalizeSpec = object : FinalizeSpecListener {
            override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
               breadcrumb += "b"
            }
         }
         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(prepareSpec, finalizeSpec)
         }
         val e = SpecRefExecutor(TestEngineContext(c, Platform.JVM))
         e.execute(SpecRef.Reference(BasicSpec::class))
         breadcrumb shouldBe "ab"
      }

      test("SpecRefExecutor should notify of errors in PrepareSpecListener") {
         var r: TestResult? = null
         val listener = object : AbstractTestEngineListener() {
            override suspend fun specFinished(ref: SpecRef, result: TestResult) {
               r = result
            }
         }
         val prepareSpec = object : PrepareSpecListener {
            override suspend fun prepareSpec(kclass: KClass<out Spec>) {
               error("kapow!")
            }
         }
         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(prepareSpec)
         }
         val e = SpecRefExecutor(TestEngineContext(c, Platform.JVM).withListener(listener))
         e.execute(SpecRef.Reference(BasicSpec::class))
         r?.errorOrNull?.message shouldBe "java.lang.IllegalStateException: kapow!"
      }

      test("SpecRefExecutor should notify of errors in FinalizeSpecListener") {
         var r: TestResult? = null
         val listener = object : AbstractTestEngineListener() {
            override suspend fun specFinished(ref: SpecRef, result: TestResult) {
               r = result
            }
         }
         val finalizeSpec = object : FinalizeSpecListener {
            override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
               error("kapow!")
            }
         }
         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(finalizeSpec)
         }
         val e = SpecRefExecutor(TestEngineContext(c, Platform.JVM).withListener(listener))
         e.execute(SpecRef.Reference(BasicSpec::class))
         r!!.errorOrNull!!.message shouldBe "java.lang.IllegalStateException: kapow!"
      }

      test("SpecRefExecutor should invoke specStarted, then the spec, then specFinished") {
         val listener = object : AbstractTestEngineListener() {
            override suspend fun specStarted(ref: SpecRef) {
               specListenerOrder += "a"
            }

            override suspend fun specFinished(ref: SpecRef, result: TestResult) {
               specListenerOrder += "c"
            }
         }
         val c = object : AbstractProjectConfig() {}
         val e = SpecRefExecutor(TestEngineContext(c, Platform.JVM).withListener(listener))
         e.execute(SpecRef.Reference(FooSpec::class))
         specListenerOrder shouldBe "abc"
      }
   }
}

@EnabledIf(AlwaysFalseCondition::class)
private class AlwaysFalseSpec : FunSpec()

private class BasicSpec : FunSpec() {
   init {
      test("a") {}
   }
}

private var specListenerOrder = ""

private class FooSpec : FunSpec() {
   init {
      test("test") {
         specListenerOrder += "b"
      }
   }
}
