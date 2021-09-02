package com.sksamuel.kotest

import com.sksamuel.kotest.extensions.SpecExtensionNumbers
import com.sksamuel.kotest.extensions.TagFilteredDiscoveryExtensionExampleTest
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.SpecExecutionOrder

object ProjectConfig : AbstractProjectConfig() {

   val intercepterLog = StringBuilder()

   override val specExecutionOrder = SpecExecutionOrder.Lexicographic

   override fun extensions(): List<Extension> {
      return listOf(SpecExtensionNumbers.ext, TagFilteredDiscoveryExtensionExampleTest.ext)
   }

   override suspend fun beforeProject() {
      intercepterLog.append("B1.")
   }
}
