package com.sksamuel.kotest.engine.concurrency

import java.util.concurrent.ConcurrentHashMap

class PersistentThreadLocal<T> : ThreadLocal<T>() {

   val map = ConcurrentHashMap<Long, T>()

   override fun set(value: T) {
      super.set(value)
      map[Thread.currentThread().id] = value
   }
}
