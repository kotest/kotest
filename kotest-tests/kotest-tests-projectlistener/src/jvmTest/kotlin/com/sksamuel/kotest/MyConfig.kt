package com.sksamuel.kotest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.ProjectListener

class MyConfig : AbstractProjectConfig() {
   override fun projectListeners(): List<ProjectListener> = listOf(TestProjectListener)
}

object TestProjectListener : ProjectListener {

   var beforeAll = 0
   var afterAll = 0

   override fun beforeProject() {
      beforeAll++
   }

   override fun afterProject() {
      afterAll++
   }
}
