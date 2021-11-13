package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.config.DefaultExtensionRegistry
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.extensions.SpecWrapperExtension
import io.kotest.engine.spec.ReflectiveSpecRef
import io.kotest.engine.spec.interceptor.ApplyExtensionsInterceptor
import io.kotest.matchers.types.shouldBeInstanceOf

class ApplyExtensionsInterceptorTest : FunSpec() {
   init {
      test("ApplyExtensionsInterceptor should apply extensions") {

         val registry = DefaultExtensionRegistry()
         ApplyExtensionsInterceptor(registry)
            .intercept(ReflectiveSpecRef(MyAnnotatedSpec::class)) {
               val wrapper = registry.all().single() as SpecWrapperExtension
               wrapper.delegate.shouldBeInstanceOf<Foo>()
               Result.success(emptyMap())
            }
      }
   }
}

class Foo : Extension

@ApplyExtension(Foo::class)
private class MyAnnotatedSpec : FunSpec()
