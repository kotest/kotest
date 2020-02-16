package com.sksamuel.kotest.runner.junit5

import io.kotest.core.spec.style.StringSpec

internal class StringSpecExceptionInInit : StringSpec({
  throw RuntimeException("kapow")
})
