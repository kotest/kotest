package io.kotest.common

import io.kotest.common.reflection.bestName
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class Foo {
   class Bar {

   }
}

class BestNameTest : FunSpec() {
   init {
      test("best name should work with nested classes") {
         Foo.Bar::class.bestName() shouldBe "io.kotest.common.Foo.Bar"
         Foo.Bar()::class.bestName() shouldBe "io.kotest.common.Foo.Bar"
      }
   }
}
