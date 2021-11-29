package io.kotest.mpp

import io.kotest.core.annotation.Ignored
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

@Isolate
@Ignored
annotation class Test1

@Test1
annotation class Test2

@Test2
private class Foo

class AnnotationReflectionTest : FunSpec() {
   init {
      test("has annotation should work for composed annotations") {
         Foo::class.annotation<Ignored>().shouldNotBeNull()
         Foo::class.hasAnnotation<Ignored>() shouldBe true
      }
   }
}
