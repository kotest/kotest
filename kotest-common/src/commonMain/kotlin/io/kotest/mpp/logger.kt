package io.kotest.mpp

@PublishedApi
internal val isLoggingEnabled by lazy { sysprop("KOTEST_DEBUG") != null || env("KOTEST_DEBUG") != null }

inline fun log(f: () -> String) {
   if (isLoggingEnabled) {
      println(f())
   }
}

inline fun log(t: Throwable?, f: () -> String) {
   if (isLoggingEnabled) {
      println(f())
      if (t != null) println(t)
   }
}
