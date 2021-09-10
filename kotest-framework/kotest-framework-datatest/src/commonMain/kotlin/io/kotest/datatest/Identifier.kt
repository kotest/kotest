package io.kotest.datatest

fun getStableIdentifier(t: Any): String {
   return when (t) {
      is WithDataTestName -> t.dataTestName()
      else -> t.toString()
   }
}
