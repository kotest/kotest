package com.sksamuel.kotest.specs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
  println("Starting main")
  runBlocking {
    val jobs = (1..10).map {
      GlobalScope.launch {
        http2000()
      }
    }
    jobs.forEach { it.join() }
  }
  println("Goodbye")
}

suspend fun http2000() {
  println("Starting http 2000")
  delay(2000)
  println("Completing http 2000")
}

suspend fun http3000() {
  println("Starting http 3000")
  delay(3000)
  println("Completing http 3000")
}

suspend fun http4000() {
  println("Starting http 4000")
  delay(4000)
  println("Completing http 4000")
}