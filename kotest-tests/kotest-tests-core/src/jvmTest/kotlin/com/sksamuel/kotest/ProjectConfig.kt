package com.sksamuel.kotest

import com.sksamuel.kotest.extensions.SpecExtensionNumbers
import com.sksamuel.kotest.extensions.TagFilteredDiscoveryExtensionExampleTest
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.filter.Filter
import io.kotest.core.spec.SpecExecutionOrder

object ProjectConfig : AbstractProjectConfig() {

   var beforeAll = 0
   var afterAll = 0

   val intercepterLog = StringBuilder()

   override val specExecutionOrder = SpecExecutionOrder.Lexicographic

   override fun extensions(): List<Extension> {
      return listOf(SpecExtensionNumbers.ext, TagFilteredDiscoveryExtensionExampleTest.ext)
   }

   override fun beforeAll() {
      intercepterLog.append("B1.")
   }
}
