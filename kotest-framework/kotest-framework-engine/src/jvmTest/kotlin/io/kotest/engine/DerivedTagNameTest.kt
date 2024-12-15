package io.kotest.engine

import io.kotest.core.Tag
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object Foo : Tag()

class DerivedTagNameTest : FunSpec({
   test("derived tags should use simple name") {
      Foo.name shouldBe "Foo"
   }
})
