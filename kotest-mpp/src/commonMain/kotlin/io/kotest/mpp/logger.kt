package io.kotest.mpp

private fun enabled() = sysprop("KOTEST_DEBUG") != null || env("KOTEST_DEBUG") != null

fun log(msg: String) = log(msg, null)

fun log(msg: String, t: Throwable?) {
   if (enabled()) {
      println(msg)
      if (t != null) println(t)
   }
}
