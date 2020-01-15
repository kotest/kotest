package io.kotest.provided

import com.sksamuel.kotest.AutoCloseListener
import com.sksamuel.kotest.TestCaseFilterTestFilter
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.FailureFirstSpecExecutionOrder
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.extensions.ProjectLevelFilter
import io.kotest.core.extensions.ProjectListener
import io.kotest.properties.PropertyTesting

object ProjectConfig : AbstractProjectConfig() {

   var beforeAll = 0
   var afterAll = 0

   val intercepterLog = StringBuilder()

   override fun projectListeners() = listOf(TestProjectListener, AutoCloseListener)

   override fun filters(): List<ProjectLevelFilter> = listOf(TestCaseFilterTestFilter)

   override fun specExecutionOrder(): SpecExecutionOrder = FailureFirstSpecExecutionOrder

   override fun beforeAll() {
      intercepterLog.append("B1.")
   }

   init {
      PropertyTesting.shouldPrintShrinkSteps = false
   }
}

object TestProjectListener : ProjectListener {

   override fun beforeProject() {
      ProjectConfig.intercepterLog.append("A1.")
      ProjectConfig.beforeAll++
   }

   override fun afterProject() {
      ProjectConfig.afterAll++
   }
}
