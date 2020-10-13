package com.sksamuel.robolectric

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.robolectric.RobolectricExtension

object ProjectConfig : AbstractProjectConfig() {
   @Suppress("CAST_NEVER_SUCCEEDS")
   override fun extensions() = listOf(RobolectricExtension() as Extension)
}
