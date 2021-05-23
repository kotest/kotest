package com.sksamuel.kotest.engine.spec.unfinished

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.FunSpec

@Ignored// this is used by the UnfinishedTestDefinitionTest, rather than being a stand alone test
internal class FunSpecUnfinishedTestDefinitionTest : FunSpec({

   context("context") {
      test("unfinished test")
   }
})
