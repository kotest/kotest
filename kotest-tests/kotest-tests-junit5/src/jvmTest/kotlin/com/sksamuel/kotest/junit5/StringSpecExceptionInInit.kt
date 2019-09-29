package com.sksamuel.kotest.junit5

import io.kotest.specs.StringSpec

class StringSpecExceptionInInit : StringSpec({
  throw RuntimeException("kapow")
})