package com.sksamuel.kotest.engine.spec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldNotExist
import io.kotest.matchers.paths.shouldBeSymbolicLink
import io.kotest.matchers.shouldBe
import java.io.File
import java.nio.file.Files

@EnabledIf(LinuxOnlyGithubCondition::class)
class TempDirTest : FunSpec({

   test("temp directory should be removed after the spec is completed") {
      val collector = CollectingTestEngineListener()
      TestEngineLauncher()
         .withListener(collector)
         .withClasses(TempDirPassSpec::class)
         .launch()

      // check the tests passed so we know the dir was created
      collector.tests.toList().single().second.isSuccess shouldBe true
      // dir should have been deleted because the specs passed
      dir1!!.shouldNotExist()
   }

   test("temp dir should be kept after the spec is completed because of keepOnFailure") {
      TestEngineLauncher()
         .withListener(NoopTestEngineListener)
         .withClasses(TempDirFailSpec::class)
         .launch()
      dir2!!.shouldExist()
   }

   test("temp directory should be removed after the spec is completed even with a sym link") {

      val collector = CollectingTestEngineListener()

      TestEngineLauncher()
         .withListener(collector)
         .withClasses(TempDirSymLink::class)
         .launch()

      // check the tests passed so we know the dir was created
      collector.tests.toList().single().second.isSuccess shouldBe true

      // dir should have been deleted because the specs passed
      dir3!!.shouldNotExist()
   }
})

private var dir1: File? = null
private var dir2: File? = null
private var dir3: File? = null

private class TempDirPassSpec : FunSpec() {
   init {

      dir1 = tempdir().apply { resolve("test.txt").writeText("This is a test file.") }

      test("pass") {
         dir1!!.shouldExist()
      }
   }
}

private class TempDirSymLink : FunSpec() {
   init {

      dir3 = tempdir().apply {
         resolve("test.txt").writeText("This is a test file.")
         val a = this.resolve("a").mkdir() shouldBe true
         val b = Files.createSymbolicLink(this.resolve("b").toPath(), this.resolve("a").toPath())
         b.shouldBeSymbolicLink()
      }

      test("pass") {
         dir3!!.shouldExist()
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
