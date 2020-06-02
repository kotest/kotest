package com.sksamuel.kotest.core.runtime

import io.kotest.assertions.assertSoftly
import io.kotest.core.config.Project
import io.kotest.core.engine.KotestEngine
import io.kotest.core.engine.TestEngineListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.runtime.AfterProjectListenerException
import io.kotest.core.runtime.BeforeProjectListenerException
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject

@DoNotParallelize
class BeforeAfterProjectTest : FunSpec({
   val errors: MutableList<Throwable> = mutableListOf()
   val listener = object : TestEngineListener {
      override fun engineFinished(t: List<Throwable>) {
         errors.addAll(t)
      }
   }

   beforeTest { mockkObject(Project) }
   afterTest {
      unmockkObject(Project)
      errors.clear()
   }

   test("2 errors from failed beforeProject and AfterProject listeners should be collected") {
      every { Project.listeners() } returns emptyList()

      val listeners = listOf(
         object : ProjectListener {
            override suspend fun beforeProject() {
               error("boom")
            }
         },
         object : ProjectListener {
            override suspend fun afterProject() {
               error("doom")
            }
         }
      )

      val engine = KotestEngine(listOf(DummySpec::class), emptyList(), 1, null, listener, listeners)
      engine.execute()
      assertSoftly {
         errors shouldHaveSize 2
         errors.filterIsInstance<BeforeProjectListenerException>() shouldHaveSize 1
         errors.filterIsInstance<AfterProjectListenerException>() shouldHaveSize 1
      }
   }
})

private class DummySpec : FunSpec({
   test("foo") {}
})
