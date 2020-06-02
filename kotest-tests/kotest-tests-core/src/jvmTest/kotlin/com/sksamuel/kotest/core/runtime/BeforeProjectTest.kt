package com.sksamuel.kotest.core.runtime

import io.kotest.assertions.assertSoftly
import io.kotest.core.config.Project
import io.kotest.core.engine.KotestEngine
import io.kotest.core.engine.TestEngineListener
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.runtime.BeforeProjectListenerException
import io.kotest.core.spec.DoNotParallelize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject

@DoNotParallelize
class BeforeAllTest : FunSpec({
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

   test("beforeProject error should use BeforeProjectListenerException") {
      every { Project.listeners() } returns emptyList()

      val listeners = listOf(object : ProjectListener {
         override val name
            get() = "BeforeAllTest ProjectListener"

         override suspend fun beforeProject() {
            error("boom")
         }
      })

      val engine = KotestEngine(listOf(DummySpec3::class), emptyList(), 1, null, listener, listeners)
      engine.execute()
      assertSoftly {
         errors shouldHaveSize 1
         errors[0].shouldBeInstanceOf<BeforeProjectListenerException>()
      }
   }

   test("2 failed beforeProject listener should be collected") {
      every { Project.listeners() } returns emptyList()

      val listeners = listOf(
         object : ProjectListener {
            override val name
               get() = "BeforeAllTest1 ProjectListener"
            //override val name = "BeforeAllTest1 ProjectListener"

            override suspend fun beforeProject() {
               error("boom")
            }
         },
         object : ProjectListener {
            override val name
               get() = "BeforeAllTest2 ProjectListener"
            //override val name = "BeforeAllTest2 ProjectListener"

            override suspend fun beforeProject() {
               error("doom")
            }
         }
      )

      val engine = KotestEngine(listOf(DummySpec3::class), emptyList(), 1, null, listener, listeners)
      engine.execute()
      assertSoftly {
         errors shouldHaveSize 2
         errors.filterIsInstance<BeforeProjectListenerException>() shouldHaveSize 2
      }
   }
})

private class DummySpec3 : FunSpec({
   test("foo") {}
})
