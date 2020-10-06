package io.kotest.androidtest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.experimental.robolectric.RobolectricExtension

object ProjectConfig : AbstractProjectConfig() {
   override fun extensions(): List<Extension> = listOf<Extension>(RobolectricExtension())
}
