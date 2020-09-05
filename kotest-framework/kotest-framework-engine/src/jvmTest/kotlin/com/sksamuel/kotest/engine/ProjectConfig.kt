package com.sksamuel.kotest.engine

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.filter.Filter

object ProjectConfig : AbstractProjectConfig() {
   override fun filters(): List<Filter> = listOf(TestFilterTestFilter)
}
