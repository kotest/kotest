package com.sksamuel.kotest.parallelism

import io.kotest.core.spec.style.StringSpec

class Test1 : StringSpec({
   "test 1" {
      startAndLockTest()
   }
})

class Test2 : StringSpec({
   "test 2" {
      startAndLockTest()
   }
})

class Test3 : StringSpec({
   "test 3" {
      startAndLockTest()
   }
})

class Test4 : StringSpec({
   "test 4" {
      startAndLockTest()
   }
})

class Test5 : StringSpec({
   "test 5" {
      startAndLockTest()
   }
})

class Test6 : StringSpec({
   "test 6" {
      startAndLockTest()
   }
})

class Test7 : StringSpec({
   "test 7" {
      startAndLockTest()
   }
})

class Test8 : StringSpec({
   "test 8" {
      startAndLockTest()
   }
})
