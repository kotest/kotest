package com.sksamuel.kotest.engine.spec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldNotExist

@EnabledIf(LinuxOnlyGithubCondition::class)
class TempFileTest : FunSpec({

   val file = tempfile()

   test("temp file should be deleted after spec") {
      file.shouldExist()
   }

   afterProject {
      file.shouldNotExist()
   }
})

