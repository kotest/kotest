package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.inspectors.forOne
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf

@EnabledIf(LinuxOnlyGithubCondition::class)
@Isolate
class AfterProjectListenerExceptionTest : FunSpec({

   test("exception in afterProject should use AfterProjectListenerException") {

      val projectListener = object : ProjectListener {
         override suspend fun afterProject() {
            error("ARRGH")
         }
      }

      val errors: MutableList<Throwable> = mutableListOf()

      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineFinished(t: List<Throwable>) {
            errors.addAll(t)
         }
      }

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(projectListener)
      }

      TestEngineLauncher(listener)
         .withClasses(DummySpec7::class)
         .withProjectConfig(c)
         .launch()

      errors shouldHaveSize 1
      errors[0].shouldBeInstanceOf<ExtensionException.AfterProjectException>()
      errors[0].cause!! shouldHaveMessage "ARRGH"
   }

   test("multiple afterProject exceptions should be collected") {

      val projectListener1 = object : ProjectListener {
         override suspend fun afterProject() {
            error("GLIPP")
         }
      }

      val projectListener2 = object : ProjectListener {
         override suspend fun afterProject() {
            error("WHACK")
         }
      }

      val errors: MutableList<Throwable> = mutableListOf()

      val listener = object : AbstractTestEngineListener() {
         override suspend fun engineFinished(t: List<Throwable>) {
            errors.addAll(t)
         }
      }

      val c = object : AbstractProjectConfig() {
         override val extensions = listOf(projectListener1, projectListener2)
      }

      TestEngineLauncher(listener)
         .withClasses(DummySpec7::class)
         .withProjectConfig(c)
         .launch()

      errors shouldHaveSize 2
      errors.filterIsInstance<ExtensionException.AfterProjectException>() shouldHaveSize 2

      errors.forOne {
         it.cause!!.shouldHaveMessage("GLIPP")
      }

      errors.forOne {
         it.cause!!.shouldHaveMessage("WHACK")
      }
   }
})

private class DummySpec7 : FunSpec({
   test("foo") {}
})
