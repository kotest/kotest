package com.sksamuel.kotest.parallelism

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.StringSpec

@EnabledIf(LinuxCondition::class)
class TestSpec1 : StringSpec({
   "test 1" {
      startAndLockTest()
   }
})

@EnabledIf(LinuxCondition::class)
class TestSpec2 : StringSpec({
   "test 2" {
      startAndLockTest()
   }
})

@EnabledIf(LinuxCondition::class)
class TestSpec3 : StringSpec({
   "test 3" {
      startAndLockTest()
   }
})

@EnabledIf(LinuxCondition::class)
class TestSpec4 : StringSpec({
   "test 4" {
      startAndLockTest()
   }
})

@EnabledIf(LinuxCondition::class)
class TestSpec5 : StringSpec({
   "test 5" {
      startAndLockTest()
   }
})

@EnabledIf(LinuxCondition::class)
class TestSpec6 : StringSpec({
   "test 6" {
      startAndLockTest()
   }
})

@EnabledIf(LinuxCondition::class)
class TestSpec7 : StringSpec({
   "test 7" {
      startAndLockTest()
   }
})

@EnabledIf(LinuxCondition::class)
class TestSpec8 : StringSpec({
   "test 8" {
      startAndLockTest()
   }
})
