package com.sksamuel.kotest

import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.Listener
import io.kotest.extensions.allure.AllureTestReporter

class ProjectConfig : AbstractProjectConfig() {

   override val specExecutionOrder = SpecExecutionOrder.Annotated

   override fun listeners(): List<Listener> {
      return listOf(AllureTestReporter(includeContainers = false))
   }
}
