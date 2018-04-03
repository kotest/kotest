package io.kotlintest.extensions.system

import io.kotlintest.extensions.TestListener
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class SystemOutWriteException : RuntimeException()
class SystemErrWriteException : RuntimeException()

object NoSystemOutListener : TestListener {
  override fun beforeProject() {
    val out = ByteArrayOutputStream()
    System.setOut(object : PrintStream(out) {
      override fun print(b: Boolean) = throw SystemOutWriteException()
      override fun print(c: Char) = throw SystemOutWriteException()
      override fun print(i: Int) = throw SystemOutWriteException()
      override fun print(l: Long) = throw SystemOutWriteException()
      override fun print(s: String) = throw SystemOutWriteException()
      override fun print(o: Any) = throw SystemOutWriteException()
      override fun print(c: CharArray) = throw SystemOutWriteException()
      override fun print(d: Double) = throw SystemOutWriteException()
      override fun print(f: Float) = throw SystemOutWriteException()
    })
  }
}

object NoSystemErrListener : TestListener {
  override fun beforeProject() {
    val out = ByteArrayOutputStream()
    System.setErr(object : PrintStream(out) {
      override fun print(b: Boolean) = throw SystemErrWriteException()
      override fun print(c: Char) = throw SystemErrWriteException()
      override fun print(i: Int) = throw SystemErrWriteException()
      override fun print(l: Long) = throw SystemErrWriteException()
      override fun print(s: String) = throw SystemErrWriteException()
      override fun print(o: Any) = throw SystemErrWriteException()
      override fun print(c: CharArray) = throw SystemErrWriteException()
      override fun print(d: Double) = throw SystemErrWriteException()
      override fun print(f: Float) = throw SystemErrWriteException()
    })
  }
}