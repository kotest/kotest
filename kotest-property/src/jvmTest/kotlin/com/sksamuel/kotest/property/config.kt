package com.sksamuel.kotest.property

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.internal.KotestEngineSystemProperties

object ProjectConfig : AbstractProjectConfig() {
  init {
     System.setProperty(KotestEngineSystemProperties.scriptsEnabled, "true")
  }
}
