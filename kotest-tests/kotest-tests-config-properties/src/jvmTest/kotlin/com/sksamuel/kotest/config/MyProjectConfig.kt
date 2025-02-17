package com.sksamuel.kotest.config

import io.kotest.core.config.AbstractProjectConfig

class MyProjectConfig : AbstractProjectConfig() {

   override suspend fun beforeProject() {
      initialized = "yes, and before project"
   }

   companion object {
      lateinit var initialized: String
   }
}
