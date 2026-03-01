package io.kotest.engine.js

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@OptIn(DelicateCoroutinesApi::class)
internal actual fun promise(block: suspend CoroutineScope.() -> Unit): Any? {
   return GlobalScope.promise { block() }
}
