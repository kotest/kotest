package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

class MyPrepareSpecListener : PrepareSpecListener {
   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      if (kclass == PrepareSpecTest::class) {
         PrepareSpecTest.b.incrementAndGet()
      }
   }
}

@ApplyExtension(MyPrepareSpecListener::class)
class PrepareSpecTest : FunSpec() {

   companion object {
      val b = AtomicInteger(0)
   }

   init {

      afterProject {
         b.get() shouldBe 1
      }

      test("ignored test").config(enabled = false) {}
      test("a").config(enabled = true) {}
      test("b").config(enabled = true) {}
      test("c").config(enabled = true) {}
      test("d").config(enabled = true) {}
   }
}
