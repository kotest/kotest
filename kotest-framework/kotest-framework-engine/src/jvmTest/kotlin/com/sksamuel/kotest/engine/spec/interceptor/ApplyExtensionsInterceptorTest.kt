package com.sksamuel.kotest.engine.spec.interceptor

import io.kotest.core.config.Configuration
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

         val conf = Configuration()
         ApplyExtensionsInterceptor(conf)
            .intercept {
               val wrapper = conf.extensions().single() as SpecWrapperExtension
               wrapper.delegate.shouldBeInstanceOf<Foo>()
               emptyMap()
            }.invoke(ReflectiveSpecRef(MyAnnotatedSpec::class))
      }
   }
}

class Foo : Extension

@ApplyExtension(Foo::class)
private class MyAnnotatedSpec : FunSpec()
