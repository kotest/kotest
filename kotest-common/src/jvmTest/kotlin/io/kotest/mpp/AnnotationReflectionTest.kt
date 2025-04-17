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

@Isolate
private abstract class Bar

@Ignored
private interface Baz

private class Qux: Bar(), Baz

class AnnotationReflectionTest : FunSpec() {
   init {
      test("has annotation by default should work for composed annotations") {
         Foo::class.annotation<Ignored>().shouldNotBeNull()
         Foo::class.hasAnnotation<Ignored>() shouldBe true
      }

      test("has annotation should work for parent class annotations if using with IncludingSuperclasses") {
         Qux::class.annotation<Isolate>(IncludingSuperclasses).shouldNotBeNull()
         Qux::class.hasAnnotation<Isolate>(IncludingSuperclasses) shouldBe true
      }

      test("has annotation should work for implemented interface annotations if using with IncludingSuperclasses") {
         Qux::class.annotation<Ignored>(IncludingSuperclasses).shouldNotBeNull()
         Qux::class.hasAnnotation<Ignored>(IncludingSuperclasses) shouldBe true
      }
   }
}
