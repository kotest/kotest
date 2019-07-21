package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig
import java.time.Duration

object ProjectConfig : AbstractProjectConfig() {
  override val timeout: Duration = Duration.ofSeconds(1)
}
