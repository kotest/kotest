package io.kotest.assertions.json

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.Matcher

/**
 * this is mainly to see how the syntax looks, no real assertions here
 */
class ReuseMatcherSyntaxTest : StringSpec({
   fun anyMatcherAcceptingFun(matcher: Matcher<String>) {
   }

   "can re-use basic matchers" {
      anyMatcherAcceptingFun(beValidJson())
      anyMatcherAcceptingFun(beJsonArray())
      anyMatcherAcceptingFun(beJsonObject())
   }

   "can re-use content-based matchers" {
      anyMatcherAcceptingFun(beEqualJson("dummy", CompareJsonOptions()))

      // no way to externally create a JsonTree? should this matcher be internal?
      // externalFunThatAcceptsMatcher(beEqualJsonTree(TODO(), CompareJsonOptions()))
      anyMatcherAcceptingFun(beJsonArray())
   }
})
