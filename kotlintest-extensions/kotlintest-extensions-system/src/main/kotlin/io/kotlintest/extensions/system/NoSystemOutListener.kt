package io.kotlintest.extensions.system

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.extensions.TestListener
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class SystemOutWriteException : RuntimeException()
class SystemErrWriteException : RuntimeException()

object NoSystemOutListener : TestListener {
  private fun setup() {
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

  override fun beforeProject() = setup()
  override fun beforeSpec(description: Description, spec: Spec) = setup()
}

object NoSystemErrListener : TestListener {
  private fun setup() {
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

  override fun beforeProject() = setup()
  override fun beforeSpec(description: Description, spec: Spec) = setup()
}