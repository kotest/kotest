package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.long
import io.kotest.property.checkAll

class DefaultClassifierTest : FunSpec() {
   init {

      test("String classifier should be provided by default string arb") {
         val out = captureStandardOut {
            checkAll<String>(PropTestConfig(outputClassifications = true, seed = 123123123)) {}
         }
         println(out)
         out.shouldContain("ANY LENGTH LETTER OR DIGITS                                    11 (1%)")
         out.shouldContain("SINGLE CHARACTER LETTER                                        10 (1%)")
         out.shouldContain("SINGLE CHARACTER DIGIT                                          2 (1%)")
         out.shouldContain("EMPTY STRING                                                   11 (1%)")
         out.shouldContain("MAX LENGTH                                                     11 (1%)")
         out.shouldContain("OTHER                                                         955 (96%)")
      }

      test("int classifier should be provided by default int arb") {
         val out = captureStandardOut {
            checkAll<Int>(PropTestConfig(outputClassifications = true, seed = 9848976132)) {}
         }
         Arb.long()
         println(out)
         out.shouldContain("Label statistics for arg 0 (1000 inputs):")
         out.shouldContain("POSITIVE EVEN                                                 242 (24%)")
         out.shouldContain("POSITIVE ODD                                                  254 (25%)")
         out.shouldContain("NEGATIVE EVEN                                                 237 (24%)")
         out.shouldContain("MIN                                                             5 (1%)")
         out.shouldContain("NEGATIVE ODD                                                  254 (25%)")
         out.shouldContain("ZERO                                                            6 (1%)")
         out.shouldContain("MAX                                                             2 (1%)")
      }

      test("long classifier should be provided by default long arb") {
         val out = captureStandardOut {
            checkAll<Long>(PropTestConfig(outputClassifications = true, seed = 1234864124)) {}
         }
         println(out)
         out.shouldContain("Label statistics for arg 0 (1000 inputs):")
         out.shouldContain("NEGATIVE ODD                                                  234 (23%)")
         out.shouldContain("POSITIVE ODD                                                  258 (26%)")
         out.shouldContain("NEGATIVE EVEN                                                 251 (25%)")
         out.shouldContain("MIN                                                             3 (1%)")
         out.shouldContain("POSITIVE EVEN                                                 246 (25%)")
         out.shouldContain("MAX                                                             3 (1%)")
         out.shouldContain("ZERO                                                            5 (1%)")
      }
   }
}
