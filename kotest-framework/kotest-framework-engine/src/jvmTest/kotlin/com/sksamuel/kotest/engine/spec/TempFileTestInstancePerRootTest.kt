package com.sksamuel.kotest.engine.spec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.tempfile
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldNotExist

@EnabledIf(LinuxCondition::class)
class TempFileTestInstancePerRootTest : FunSpec({

   val file = tempfile()

   isolationMode = IsolationMode.InstancePerRoot

   test("temp file should be deleted after spec") {
      file.shouldExist()
   }

   afterProject {
      file.shouldNotExist()
   }
})
