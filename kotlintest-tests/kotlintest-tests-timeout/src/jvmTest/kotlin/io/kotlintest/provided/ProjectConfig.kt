package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@ExperimentalTime
object ProjectConfig : AbstractProjectConfig() {
   override val timeout = 1000.milliseconds
}
