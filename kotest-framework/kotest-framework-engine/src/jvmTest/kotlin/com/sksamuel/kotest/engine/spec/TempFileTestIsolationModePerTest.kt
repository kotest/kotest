package com.sksamuel.kotest.engine.spec

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldNotExist

class TempFileTestIsolationModePerTest : FunSpec({

   val file = tempfile()

   isolationMode = IsolationMode.InstancePerTest

   test("temp file should be deleted after spec") {
      file.shouldExist()
   }

   afterProject {
      file.shouldNotExist()
   }
})
