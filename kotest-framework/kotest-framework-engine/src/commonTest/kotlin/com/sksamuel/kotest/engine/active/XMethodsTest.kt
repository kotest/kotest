package com.sksamuel.kotest.engine.active

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec

class XFunSpecTest : FunSpec({
   xtest("using xtext") {
      error("boom")
   }

   xcontext("using xcontext") {
      error("boom")
   }
})

class XShouldSpecTest : ShouldSpec({
   xshould("using xshould") {
      error("boom")
   }

   xcontext("using xcontext") {
      error("boom")
   }
})
