package com.sksamuel.kotest.specs

import io.kotest.core.spec.style.FunSpec

class TempFileTest : FunSpec({

   val file = tempfile()

   test("temp file should be deleted after spec") {
      file.shouldExist()
   }

   afterProject {
      file.shouldNotExist()
   }
})
