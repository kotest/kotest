package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import java.util.concurrent.atomic.AtomicBoolean

private val initialized = AtomicBoolean(false)

class ProjectConfig : AbstractProjectConfig() {
   override val invocations = 5
   init {
      val updated = initialized.compareAndSet(false, true)
      if (!updated) error("ProjectConfig should only be instantiated once")
   }
}
