package com.sksamuel.kotest.engine.spec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldNotExist

@EnabledIf(LinuxCondition::class)
class TempDirTest : FunSpec({

   val dir = tempdir().apply { resolve("test.txt").writeText("This is a test file.") }

   test("temp directory should be deleted after spec") {
      dir.shouldExist()
   }

   afterProject {
      dir.shouldNotExist()
   }
})
