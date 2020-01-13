package com.sksamuel.kotest.junit5

import io.kotest.core.spec.style.StringSpec

class StringSpecExceptionInInit : StringSpec({
  throw RuntimeException("kapow")
})
