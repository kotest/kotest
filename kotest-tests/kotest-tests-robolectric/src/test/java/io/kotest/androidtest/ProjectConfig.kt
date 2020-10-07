package io.kotest.androidtest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.ConstructorExtension
import io.kotest.core.extensions.Extension
import io.kotest.extensions.robolectric.RobolectricExtension

object ProjectConfig : AbstractProjectConfig() {
   override fun extensions() = listOf(RobolectricExtension() as ConstructorExtension)
}
