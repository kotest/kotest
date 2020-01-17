package com.sksamuel.kotest.autoscan

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldHaveLength

class AutoScanProjectListenerTest : FunSpec({

   test("@AutoScan project listeners should be picked up") {
      Container.results.shouldHaveLength(2)
      Container.results.shouldContain("A")
      Container.results.shouldContain("C")
   }
})
