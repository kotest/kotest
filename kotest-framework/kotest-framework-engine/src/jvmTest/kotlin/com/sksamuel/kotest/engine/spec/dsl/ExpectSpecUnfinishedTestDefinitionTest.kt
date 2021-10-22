package com.sksamuel.kotest.engine.spec.dsl

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.ExpectSpec

@Ignored// this is used by the UnfinishedTestDefinitionTest, rather than being a stand alone test
internal class ExpectSpecUnfinishedTestDefinitionTest : ExpectSpec({

   context("context") {
      expect("unfinished expect")
   }
})
