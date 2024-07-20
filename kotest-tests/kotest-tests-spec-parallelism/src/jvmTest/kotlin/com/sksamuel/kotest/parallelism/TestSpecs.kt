package com.sksamuel.kotest.parallelism

import io.kotest.core.spec.style.StringSpec

class TestSpec1 : StringSpec({
   "test 1" {
      startAndLockTest()
   }
})

class TestSpec2 : StringSpec({
   "test 2" {
      startAndLockTest()
   }
})

class TestSpec3 : StringSpec({
   "test 3" {
      startAndLockTest()
   }
})

class TestSpec4 : StringSpec({
   "test 4" {
      startAndLockTest()
   }
})

class TestSpec5 : StringSpec({
   "test 5" {
      startAndLockTest()
   }
})

class TestSpec6 : StringSpec({
   "test 6" {
      startAndLockTest()
   }
})

class TestSpec7 : StringSpec({
   "test 7" {
      startAndLockTest()
   }
})

class TestSpec8 : StringSpec({
   "test 8" {
      startAndLockTest()
   }
})
