package com.sksamuel.kotest.core.runtime

import io.kotest.assertions.assertSoftly
import io.kotest.core.config.Project
import io.kotest.core.engine.KotestEngine
import io.kotest.core.engine.TestEngineListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.runtime.AfterProjectListenerException
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject

@DoNotParallelize
class AfterProjectTest : FunSpec({

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

   test("after project error should use AfterAllListenerException") {
      every { Project.listeners() } returns emptyList()

      val listeners = listOf(object : ProjectListener {
         override fun afterProject() {
            error("boom")
         }
      })

      val engine = KotestEngine(listOf(DummySpec2::class), emptyList(), 1, null, listener, listeners)
      engine.execute()
      assertSoftly {
         errors shouldHaveSize 1
         errors[0].shouldBeInstanceOf<AfterProjectListenerException>()
      }
   }

   test("after project errors should have size 2") {
      every { Project.listeners() } returns emptyList()
      val listeners = listOf(
         object : ProjectListener {
            override fun afterProject() {
               error("boom")
            }
         },
         object : ProjectListener {
            override fun afterProject() {
               error("doom")
            }
         }
      )

      val engine = KotestEngine(listOf(DummySpec2::class), emptyList(), 1, null, listener, listeners)
      engine.execute()
      assertSoftly {
         errors shouldHaveSize 2
         errors.filterIsInstance<AfterProjectListenerException>() shouldHaveSize 2
      }

   }
})

private class DummySpec2 : FunSpec({
   test("foo") {}
})
