package io.kotest.plugin.intellij

object Constants {

   const val FRAMEWORK_NAME = "Kotest"

   @Deprecated("This is only used by the deprecated Kotest run configurations.")
   const val FRAMEWORK_ID = "ioKotest"
}

// flip this bit in tests
var testMode = false
