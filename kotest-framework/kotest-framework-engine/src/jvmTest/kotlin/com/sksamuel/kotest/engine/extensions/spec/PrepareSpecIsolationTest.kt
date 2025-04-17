package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

class PrepareSpecLeafTestListener : PrepareSpecListener {
   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      if (kclass == PrepareSpecIsolationTest::class) {
         PrepareSpecIsolationTest.c.incrementAndGet()
      }
   }
}

@ApplyExtension(PrepareSpecLeafTestListener::class)
class PrepareSpecIsolationTest : FunSpec() {

   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerRoot

   companion object {
      val c = AtomicInteger(0)
   }

   init {

      afterProject {
         c.get() shouldBe 1
      }

      test("ignored test").config(enabled = false) {}
      test("a").config(enabled = true) {}
      test("b").config(enabled = true) {}
      test("c").config(enabled = true) {}
      test("d").config(enabled = true) {}
   }
}
