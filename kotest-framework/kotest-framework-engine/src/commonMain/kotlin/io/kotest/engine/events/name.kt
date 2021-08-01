package io.kotest.engine.events

import io.kotest.core.listeners.Listener

fun <T : Listener> List<T>.resolveName(): List<Pair<String, T>> = groupBy { it.name }
   .flatMap { entry ->
      if (entry.value.size > 1) {
         entry.value.mapIndexed { index, listener -> "${listener.name}_$index" to listener }
      } else {
         entry.value.map { it.name to it }
      }
   }
