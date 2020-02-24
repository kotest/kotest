package com.sksamuel.kotest.specs

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.tempfile
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldNotExist

class TempFileTest : FunSpec({

   val file = tempfile()

   test("temp file should be deleted after spec") {
      file.shouldExist()
   }

   afterProject {
      file.shouldNotExist()
   }
})
