package com.sksamuel.kotest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecExecutionOrder

object ProjectConfig : AbstractProjectConfig() {

   private val intercepterLog = StringBuilder()

   override val specExecutionOrder = SpecExecutionOrder.Lexicographic

   override suspend fun beforeProject() {
      intercepterLog.append("B1.")
   }
}
