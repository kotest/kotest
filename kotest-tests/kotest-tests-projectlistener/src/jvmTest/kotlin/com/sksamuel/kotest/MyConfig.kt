package com.sksamuel.kotest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.ProjectExtension
import io.kotest.core.listeners.ProjectListener

class MyConfig : AbstractProjectConfig() {
   override fun listeners() = listOf(TestProjectListener, TestBeforeProjectListener)

   override suspend fun beforeProject() {
      MyConfigGlobalState.beforeProjectCallCount++
   }

   override fun beforeAll() {
      MyConfigGlobalState.beforeAllCallCount++
   }

   override suspend fun afterProject() {
      MyConfigGlobalState.afterProjectCallCount++
   }

   override fun afterAll() {
      MyConfigGlobalState.afterAllCallCount++
   }
}

object MyConfigGlobalState {
   var beforeAllCallCount = 0
   var afterAllCallCount = 0
   var beforeProjectCallCount = 0
   var afterProjectCallCount = 0
}

internal val listExtensionEvents = mutableListOf<String>()

object TestProjectExtension : ProjectExtension {
   override suspend fun aroundProject(callback: suspend () -> Throwable?): Throwable? {
      listExtensionEvents.add("hello")
      return callback()
   }
}

object TestProjectExtension2 : ProjectExtension {
   override suspend fun aroundProject(callback: suspend () -> Throwable?): Throwable? {
      listExtensionEvents.add("there")
      return callback()
   }
}

object TestProjectListener : ProjectListener {

   var beforeAll = 0
   var afterAll = 0

   override suspend fun beforeProject() {
      beforeAll++
   }

   override suspend fun afterProject() {
      afterAll++
   }
}

object TestBeforeProjectListener : ProjectListener {

   var beforeAll = 0
   var afterAll = 0

   override suspend fun beforeProject() {
      beforeAll++
   }

   override suspend fun afterProject() {
      afterAll++
   }
}
