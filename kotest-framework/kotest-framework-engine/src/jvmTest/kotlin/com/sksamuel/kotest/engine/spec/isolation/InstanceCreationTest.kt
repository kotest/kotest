package com.sksamuel.kotest.engine.spec.isolation

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

private val instances = AtomicInteger(0)

class InstanceCreationTest : FunSpec() {

   init {

      isolationMode = IsolationMode.InstancePerRoot

      // this will be invoked each time an instance of this spec is created
      instances.incrementAndGet()

      afterProject {
         instances.get() shouldBe 2
      }

      test("this test should use the 'default' instance") {
      }

      test("this test should use a 'new' instance") {
      }
   }
}
