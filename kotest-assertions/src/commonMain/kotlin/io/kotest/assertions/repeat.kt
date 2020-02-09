@file:JvmName("RepeatJvmKt")
package io.kotest.assertions

import kotlin.jvm.JvmName

internal fun simpleRepeat(
   times: Int,
   before: (Int) -> Unit = { },
   after: (Int) -> Unit = {},
   action: (Int) -> Unit
) {
   repeat(times) {
      before(it)
      action(it)
      after(it)
   }
}

expect fun replay(
   times: Int,
   threads: Int,
   before: (Int) -> Unit = { },
   after: (Int) -> Unit = {},
   action: (Int) -> Unit
)
