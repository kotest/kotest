package com.sksamuel.kotest.engine.spec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldNotExist
import io.kotest.matchers.shouldBe
import java.io.File

@EnabledIf(LinuxCondition::class)
class TempDirTest : FunSpec({

   test("temp directory should be removed after the spec is completed") {
      val collector = CollectingTestEngineListener()
      TestEngineLauncher(collector)
         .withConfiguration(ProjectConfiguration())
         .withClasses(TempDirPassSpec::class)
         .launch()

      // check the tests passed so we know the dir was created
      collector.tests.toList().single().second.isSuccess shouldBe true
      // dir should have been deleted because the specs passed
      dir1!!.shouldNotExist()
   }

   test("temp dir should be kept after the spec is completed because of keepOnFailure") {
      TestEngineLauncher(NoopTestEngineListener)
         .withConfiguration(ProjectConfiguration())
         .withClasses(TempDirFailSpec::class)
         .launch()
      dir2!!.shouldExist()
   }
})

private var dir1: File? = null
private var dir2: File? = null

private class TempDirPassSpec : FunSpec() {
   init {

      dir1 = tempdir().apply { resolve("test.txt").writeText("This is a test file.") }

      test("pass") {
         dir1!!.shouldExist()
      }
   }
}

private class TempDirFailSpec : FunSpec() {
   init {

      dir2 = tempdir(keepOnFailure = true).apply { resolve("test.txt").writeText("This is a test file.") }

      test("fail") {
         error("boom")
      }
   }
}
