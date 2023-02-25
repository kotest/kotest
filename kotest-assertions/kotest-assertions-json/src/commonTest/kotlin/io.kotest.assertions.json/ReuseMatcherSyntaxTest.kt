package io.kotest.assertions.json

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

class ReuseMatcherSyntaxTest : FreeSpec({

   "syntax" - {
      //  these are mainly to see how the syntax looks, no real assertions here
      fun matcherReUse1(matcher: Matcher<String>) {
      }

      fun matcherReUse2(matcher: Matcher<String?>) {
      }

      "can re-use basic matchers" {
         matcherReUse1(beValidJson())
         matcherReUse2(beValidJson())
         matcherReUse1(beJsonArray())
         matcherReUse2(beJsonArray())
         matcherReUse1(beJsonObject())
         matcherReUse2(beJsonObject())
      }

      "can re-use content-based matchers" {
         matcherReUse1(beEqualJson("dummy", CompareJsonOptions()))
         matcherReUse2(beEqualJson("dummy", CompareJsonOptions()))

         // no way to externally create a JsonTree? should this matcher be internal?
         // externalFunThatAcceptsMatcher(beEqualJsonTree(TODO(), CompareJsonOptions()))
         matcherReUse1(beJsonArray())
         matcherReUse2(beJsonArray())
      }
   }

   "matcher" -  {
      "null should beEqualJson null" {
         val subject: String? = null
         subject should beEqualJson(null, CompareJsonOptions())
      }

      "notNull shouldNot beEqualJson null" {
         val subject: String? = null
         subject shouldNot beEqualJson("null", CompareJsonOptions())
      }
   }
})
