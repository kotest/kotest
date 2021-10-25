package io.kotest.core.test

@Deprecated("use EnabledOrReasonIf. Deprecated since 5.0")
typealias EnabledIf = (TestCase) -> Boolean

typealias EnabledOrReasonIf = (TestCase) -> Enabled

data class Enabled(val isEnabled: Boolean, val reason: String? = null) {
   val isDisabled = !isEnabled
   companion object {
      val enabled = Enabled(true)
      val disabled = Enabled(false, null)
      fun disabled(reason: String?) = Enabled(false, reason)
   }
}
