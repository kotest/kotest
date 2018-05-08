package com.sksamuel.kotlintest.tests.parallelism

import io.kotlintest.specs.StringSpec

class Test1 : StringSpec({
  "Test1" {
    Thread.sleep(2000)
  }
})

class Test2 : StringSpec({
  "Test1" {
    Thread.sleep(2000)
  }
})

class Test3 : StringSpec({
  "Test1" {
    Thread.sleep(2000)
  }
})

class Test4 : StringSpec({
  "Test1" {
    Thread.sleep(2000)
  }
})

class Test5 : StringSpec({
  "Test1" {
    Thread.sleep(2000)
  }
})