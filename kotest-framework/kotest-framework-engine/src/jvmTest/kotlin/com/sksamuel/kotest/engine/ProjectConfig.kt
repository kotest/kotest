package com.sksamuel.kotest.engine

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension

class ProjectConfig : AbstractProjectConfig() {
   override fun extensions(): List<Extension> = listOf(TestFilterTestFilter)
   override val parallelism: Int = 1
}
