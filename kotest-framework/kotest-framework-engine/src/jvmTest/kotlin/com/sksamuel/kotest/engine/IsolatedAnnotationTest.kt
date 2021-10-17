package com.sksamuel.kotest.engine

import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.AutoScan
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import java.util.concurrent.atomic.AtomicBoolean

@Isolate
class IsolatedAnnotationTest : FunSpec() {
   init {
      test("classes annotated with @Isolate should run") {
         invoked.set(true)
      }
   }
}

@AutoScan
class IsolatedAnnotationTestAfterProject : ProjectListener {
   override suspend fun afterProject() {
//      invoked.get() shouldBe true
   }
}

val invoked = AtomicBoolean(false)
