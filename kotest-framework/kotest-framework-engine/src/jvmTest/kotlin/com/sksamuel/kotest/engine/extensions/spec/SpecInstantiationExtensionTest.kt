package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.config.configuration
import io.kotest.core.extensions.SpecInstantiationExtension
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass

@Isolate
class SpecInstantiationExtensionTest : FunSpec() {
   init {
      test("SpecInstantiationExtension should trigger for successful instantiation") {
         var count = 0
         val ext = object : SpecInstantiationExtension {
            override suspend fun onSpecInstantiation(spec: Spec) {
               count++
            }
         }
         configuration.register(ext)
         SpecExecutor(NoopTestEngineListener, NoopCoroutineDispatcherFactory)
            .execute(ClassWithSimpleConstructor::class)
         configuration.deregisterExtension(ext)
         count shouldBe 1
      }
      test("SpecInstantiationExtension should trigger when instantiation fails") {
         var count = 0
         val ext = object : SpecInstantiationExtension {
            override suspend fun onSpecInstantiationError(kclass: KClass<*>, t: Throwable) {
               count++
            }
         }
         configuration.register(ext)
         SpecExecutor(NoopTestEngineListener, NoopCoroutineDispatcherFactory)
            .execute(ClassWithComplexConstructor::class)
         configuration.deregisterExtension(ext)
         count shouldBe 1
      }
   }
}

private class ClassWithComplexConstructor(val thread: Thread) : FunSpec() {
   init {
      test("a") {}
   }
}

private class ClassWithSimpleConstructor : FunSpec() {
   init {
      test("a") {}
   }
}

