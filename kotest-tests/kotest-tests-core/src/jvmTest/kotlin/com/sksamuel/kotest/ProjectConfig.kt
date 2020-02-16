package com.sksamuel.kotest

import com.sksamuel.kotest.extensions.SpecExtensionNumbers
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.filters.Filter
import io.kotest.core.spec.FailureFirstSpecExecutionOrder
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.properties.PropertyTesting

object ProjectConfig : AbstractProjectConfig() {

   var beforeAll = 0
   var afterAll = 0

   val intercepterLog = StringBuilder()

   override fun filters(): List<Filter> = listOf(TestCaseFilterTestFilter)

   override fun specExecutionOrder(): SpecExecutionOrder = FailureFirstSpecExecutionOrder

   override fun extensions(): List<Extension> {
      return listOf(SpecExtensionNumbers.ext)
   }

   override fun beforeAll() {
      intercepterLog.append("B1.")
   }

   init {
      PropertyTesting.shouldPrintShrinkSteps = false
   }
}
