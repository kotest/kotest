package com.sksamuel.kotest.engine.spec.unfinished

import io.kotest.core.annotation.Ignored
import io.kotest.core.spec.style.DescribeSpec

@Ignored// this is used by the UnfinishedTestDefinitionTest, rather than being a stand alone test
internal class DescribeSpecUnfinishedTestDefinitionTest : DescribeSpec({

   describe("describe") {
      it("unfinished it")
   }

})
