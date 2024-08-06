package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.listeners.PrepareSpecListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

class PrepareSpecTestListener : PrepareSpecListener {
   override suspend fun prepareSpec(kclass: KClass<out Spec>) {
      if (kclass == PrepareSpecIsolationTestTest::class) {
         PrepareSpecIsolationTestTest.a.incrementAndGet()
      }
   }
}

@ApplyExtension(PrepareSpecTestListener::class)
class PrepareSpecIsolationTestTest : FunSpec() {
   override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

   companion object {
      val a = AtomicInteger(0)
   }

   init {

      afterProject {
         a.get() shouldBe 1
      }

      test("ignored test").config(enabled = false) {}
      test("a").config(enabled = true) {}
      test("b").config(enabled = true) {}
      test("c").config(enabled = true) {}
      test("d").config(enabled = true) {}
   }
}
