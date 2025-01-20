package io.kotest.plugin.intellij

object Constants {

   val FrameworkName = "Kotest"
   val FileLocatorProtocol = "kotest:file"
   val ClassLocatorProtocol = "kotest:class"

   // kotst 4.1.x -> 4.6.x protocol string
   val OldLocatorProtocol = "kotest"
   val FrameworkId = "ioKotest"

   const val GRADLE_TASK_NAME = "kotest"
}

// flip this bit in tests
var testMode = false
