package com.sksamuel.kotest.engine.active

import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.WordSpec

class XMethodsWordSpecTest : WordSpec({
   "using xshould" xshould {
      "disable test" {
         error("boom")
      }
   }
})

class XMethodsFeatureSpecTest : FeatureSpec({
   xfeature("using xfeature") {
      error("boom")
   }

   feature("using feature") {
      xscenario("using xscenario") {
         error("boom")
      }
   }
})

class XMethodsExpectSpecTest : ExpectSpec({
   xcontext("using xcontext") {
      error("boom")
   }

   xexpect("using xexpect") {
      error("boom")
   }
})
