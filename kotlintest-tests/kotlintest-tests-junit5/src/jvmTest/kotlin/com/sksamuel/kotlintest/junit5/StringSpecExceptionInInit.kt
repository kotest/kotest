package com.sksamuel.kotlintest.junit5

import io.kotlintest.specs.StringSpec

class StringSpecExceptionInInit : StringSpec({
  throw RuntimeException("kapow")
})