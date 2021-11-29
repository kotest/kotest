package com.sksamuel.kotest.engine.spec.coroutine

fun currentThreadWithoutCoroutine(): String {
   val name = Thread.currentThread().name
   val index = name.indexOf("@coroutine")
   return if (index == -1) name else name.take(index)
}
