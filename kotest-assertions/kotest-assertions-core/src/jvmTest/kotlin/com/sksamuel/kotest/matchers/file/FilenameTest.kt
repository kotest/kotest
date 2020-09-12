package com.sksamuel.kotest.matchers.file

import io.kotest.assertions.shouldFail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.file.shouldHaveNameWithoutExtension
import io.kotest.matchers.file.shouldNotHaveNameWithoutExtension
import io.kotest.matchers.paths.shouldHaveNameWithoutExtension
import io.kotest.matchers.paths.shouldNotHaveNameWithoutExtension
import java.io.File
import java.nio.file.Paths

class FilenameTest : FunSpec() {
   init {
      test("shouldHaveNameWithoutExtension(name)") {
         File("sammy/boy").shouldHaveNameWithoutExtension("boy")
         Paths.get("sammy/boy").shouldHaveNameWithoutExtension("boy")

         File("sammy/boy.txt").shouldHaveNameWithoutExtension("boy")
         Paths.get("sammy/boy.txt").shouldHaveNameWithoutExtension("boy")

         shouldFail {
            File("sammy/boy.txt").shouldNotHaveNameWithoutExtension("boy")
         }

         shouldFail {
            Paths.get("sammy/boy.txt").shouldNotHaveNameWithoutExtension("boy")
         }
      }
   }
}
