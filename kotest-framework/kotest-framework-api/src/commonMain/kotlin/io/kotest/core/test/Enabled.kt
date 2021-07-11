package io.kotest.core.test

typealias EnabledIf = (TestCase) -> Boolean
typealias EnabledOrReasonIf = (TestCase) -> Enabled

class Enabled private constructor(val isEnabled: Boolean, reason: String? = null) {
   private val builder = StringBuilder(reason ?: "")
   val reason get() = builder.trim().toString()

   companion object {
      val enabled = Enabled(true)
      val disabled = Enabled(false, null)
      fun disabled(reason: String) = Enabled(false, reason)

      fun fold(es: Iterable<Enabled>): Enabled {
         return es.fold(enabled) { acc, e ->
            Enabled(acc.isEnabled && e.isEnabled, acc.reason).also {
               if (!e.isEnabled) {
                  it.builder.appendLine()
                  it.builder.append(e.reason)
               }
            }
         }
      }
   }
}
