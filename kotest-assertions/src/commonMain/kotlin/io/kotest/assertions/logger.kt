package io.kotest.assertions

import io.kotest.mpp.sysprop

fun log(msg: String) {
   if (sysprop("kotest.log") == "true") println(msg)
}

fun log(msg: String, t: Throwable?) {
   if (sysprop("kotest.log") == "true") {
      println(msg)
      if (t != null) println(t)
   }
}
