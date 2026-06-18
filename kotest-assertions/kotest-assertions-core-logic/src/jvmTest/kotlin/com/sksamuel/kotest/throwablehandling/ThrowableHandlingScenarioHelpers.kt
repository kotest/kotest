package com.sksamuel.kotest.throwablehandling

class FooRuntimeException : RuntimeException()

open class ParentException : Throwable()
class SubException : ParentException()

fun catchThrowable(block: () -> Any?): Throwable? {
  return try {
    block()
    null
  } catch (t: Throwable) { t }
}