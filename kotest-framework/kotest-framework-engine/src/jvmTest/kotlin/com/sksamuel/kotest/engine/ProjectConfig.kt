package com.sksamuel.kotest.engine

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.filter.Filter

class ProjectConfig : AbstractProjectConfig() {
   override fun filters(): List<Filter> = listOf(TestFilterTestFilter)
   override val parallelism: Int = 1
}
