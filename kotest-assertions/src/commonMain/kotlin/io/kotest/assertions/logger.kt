package io.kotest.assertions

fun log(msg: String) {
   if (sysprop("kotest.log") == "true") println(msg)
}

fun log(msg: String, t: Throwable?) {
   if (sysprop("kotest.log") == "true") {
      println(msg)
      if (t != null) println(t)
   }
}
