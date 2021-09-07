package io.kotest.common

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

actual fun <T> runBlocking(f: suspend () -> T): T = error("runBlocking is not available on JS")

actual fun promise(f: suspend () -> Unit) {
   GlobalScope.promise { f() }
}
