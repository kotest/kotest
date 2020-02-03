package io.kotest.assertions

import io.kotest.mpp.env
import io.kotest.mpp.sysprop

private fun enabled() = sysprop("kotest.log") != null || env("kotest.log") != null

fun log(msg: String) = log(msg, null)

fun log(msg: String, t: Throwable?) {
   if (enabled()) {
      println(msg)
      if (t != null) println(t)
   }
}
