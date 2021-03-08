package com.sksamuel.kotest.property

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.internal.KotestEngineProperties

object ProjectConfig : AbstractProjectConfig() {
  init {
     System.setProperty(KotestEngineProperties.scriptsEnabled, "true")
  }
}
