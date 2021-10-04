package io.kotest.engine.extensions

import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.InactiveSpecListener
import io.kotest.core.extensions.SpecCreationErrorListener
import io.kotest.core.extensions.SpecExtension
import io.kotest.core.extensions.SpecFinalizeExtension
import io.kotest.core.extensions.SpecIgnoredListener
import io.kotest.core.extensions.SpecInitializeExtension
import io.kotest.core.extensions.SpecInterceptExtension
import io.kotest.core.listeners.AfterContainerListener
import io.kotest.core.listeners.AfterEachListener
import io.kotest.core.listeners.AfterSpecListener
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeContainerListener
import io.kotest.core.listeners.BeforeEachListener
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.listeners.FinalizeSpecListener
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.listeners.SpecInstantiationListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

/**
 * Wraps another extension, delegating spec extensions only for the specified spec.
 */
internal class SpecWrapperExtension(
   private val delegate: Extension,
   private val target: KClass<*>
) : SpecInstantiationListener,
   SpecCreationErrorListener,
   SpecExtension,
   SpecInterceptExtension,
   SpecFinalizeExtension,
   SpecIgnoredListener,
   SpecInitializeExtension,
   InactiveSpecListener,
   AfterSpecListener,
   BeforeSpecListener,
   PrepareSpecListener,
   FinalizeSpecListener,
   BeforeTestListener,
   AfterTestListener,
   BeforeEachListener,
   AfterEachListener,
   BeforeContainerListener,
   AfterContainerListener {

   override suspend fun beforeContainer(testCase: TestCase) {
      if (delegate is BeforeContainerListener && testCase.spec::class == target) delegate.beforeContainer(testCase)
   }

   override suspend fun afterContainer(testCase: TestCase, result: TestResult) {
      if (delegate is AfterContainerListener && testCase.spec::class == target) delegate.afterContainer(
         testCase,
         result
      )
   }

   override suspend fun afterEach(testCase: TestCase, result: TestResult) {
      if (delegate is AfterEachListener && testCase.spec::class == target) delegate.afterEach(testCase, result)
   }

   override suspend fun afterAny(testCase: TestCase, result: TestResult) {
      if (delegate is AfterTestListener && testCase.spec::class == target) delegate.afterAny(testCase, result)
   }

   override suspend fun afterTest(testCase: TestCase, result: TestResult) {
      if (delegate is AfterTestListener && testCase.spec::class == target) delegate.afterTest(testCase, result)
   }

   override suspend fun beforeEach(testCase: TestCase) {
      if (delegate is BeforeEachListener && testCase.spec::class == target) delegate.beforeEach(testCase)
   }

   override suspend fun beforeAny(testCase: TestCase) {
      if (delegate is BeforeTestListener && testCase.spec::class == target) delegate.beforeAny(testCase)
   }

   override suspend fun beforeTest(testCase: TestCase) {
      if (delegate is BeforeTestListener && testCase.spec::class == target) delegate.beforeTest(testCase)
   }

   override suspend fun onSpecCreationError(kclass: KClass<*>, t: Throwable) {
      if (delegate is SpecCreationErrorListener && kclass == target) delegate.onSpecCreationError(kclass, t)
   }

   override fun finalizeSpec(spec: Spec) {
      if (delegate is SpecFinalizeExtension && spec::class == target) delegate.finalizeSpec(spec)
   }

   override suspend fun ignoredSpec(kclass: KClass<*>, reason: String?) {
      if (delegate is SpecIgnoredListener && kclass == target) delegate.ignoredSpec(kclass, reason)
   }

   override suspend fun initializeSpec(spec: Spec): Spec {
      return if (delegate is SpecInitializeExtension && spec::class == target) delegate.initializeSpec(spec) else spec
   }

   override suspend fun interceptSpec(spec: Spec, process: suspend (Spec) -> Unit) {
      if (delegate is SpecInterceptExtension && spec::class == target)
         delegate.interceptSpec(spec, process)
      else process(spec)
   }

   override suspend fun afterSpec(spec: Spec) {
      if (delegate is AfterSpecListener && spec::class == target) delegate.afterSpec(spec)
   }

   override suspend fun beforeSpec(spec: Spec) {
      if (delegate is BeforeSpecListener && spec::class == target) delegate.beforeSpec(spec)
   }

   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      if (delegate is FinalizeSpecListener && kclass == target) delegate.finalizeSpec(kclass, results)
   }

   override suspend fun inactive(spec: Spec, results: Map<TestCase, TestResult>) {
      if (delegate is InactiveSpecListener && spec::class == target) delegate.inactive(spec, results)
   }

   override fun specInstantiated(spec: Spec) {
      if (delegate is SpecInstantiationListener && spec::class == target) delegate.specInstantiated(spec)
   }

   override suspend fun intercept(spec: Spec, execute: suspend (Spec) -> Unit) {
      if (delegate is SpecExtension && spec::class == target) delegate.intercept(spec, execute) else execute(spec)
   }

   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      if (delegate is PrepareSpecListener && kclass == target) delegate.prepareSpec(kclass)
   }

   override suspend fun intercept(spec: KClass<out Spec>, process: suspend () -> Unit) {
      if (delegate is SpecExtension && spec == target) delegate.intercept(spec, process) else process()
   }

   override fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {
      if (delegate is SpecInstantiationListener && kclass == target) delegate.specInstantiationError(kclass, t)
   }
}
