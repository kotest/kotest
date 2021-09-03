package com.sksamuel.kotest.engine.listeners.project

import io.kotest.core.config.AbstractProjectConfig

class MyConfig : AbstractProjectConfig() {

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
