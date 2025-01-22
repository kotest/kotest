package io.kotest.plugin.intellij

object Constants {

   const val FRAMEWORK_NAME = "Kotest"
   const val KOTEST_CLASS_LOCATOR_PROTOCOL = "kotest"
   const val GRADLE_TASK_NAME = "kotest"

   val FrameworkId = "ioKotest"

}

// flip this bit in tests
var testMode = false
