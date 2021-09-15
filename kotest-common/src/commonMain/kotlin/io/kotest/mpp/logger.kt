package io.kotest.mpp

@PublishedApi
internal fun isLoggingEnabled() =
   sysprop("KOTEST_DEBUG")?.uppercase() == "TRUE" || env("KOTEST_DEBUG")?.uppercase() == "TRUE"

inline fun log(f: () -> String) {
   if (isLoggingEnabled()) {
      println(timeInMillis().toString() + " " + f())
   }
}

inline fun log(t: Throwable?, f: () -> String) {
   if (isLoggingEnabled()) {
      println(timeInMillis().toString() + " " + f())
      if (t != null) println(t)
   }
}
