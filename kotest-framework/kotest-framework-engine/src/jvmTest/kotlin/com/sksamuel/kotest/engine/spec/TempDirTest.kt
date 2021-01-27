package com.sksamuel.kotest.engine.spec

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldNotExist

class TempDirTest : FunSpec({

   val dir = tempdir()

   test("temp directory should be deleted after spec") {
      dir.shouldExist()
   }

   afterProject {
      dir.shouldNotExist()
   }
})
