package io.kotest.assertions.json.schema

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.longs.beGreaterThanOrEqualTo
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.take

@OptIn(ExperimentalKotest::class)
class ObjectSchemaBuilderTest : StringSpec(
   {
      "withProperty generates same schema as without" {
         val (a, b, c, d, e) = Arb.string().take(5).toList()
         val (f, g, h, i, _) = Arb.string().take(5).toList()

         val longMatcher = beGreaterThanOrEqualTo(5)

         jsonSchema {
            obj {
               withProperty(a) { string() }
               withProperty(b) { integer { longMatcher } }
               withProperty(c) { decimal() }
               withProperty(d) { array { integer() } }
               withProperty(e) { `null`() }
               withProperty(f) {
                  obj {
                     withProperty(g, optional = true) { number() }
                     withProperty(h) {
                        obj {
                           withProperty(i) { string() }
                           additionalProperties = false
                        }
                     }
                  }
               }
            }
         } shouldBe jsonSchema {
            obj {
               string(a)
               integer(b) { longMatcher }
               decimal(c)
               array(d) { integer() }
               `null`(e)
               obj(f) {
                  number(g, optional = true)
                  obj(h) {
                     string(i)
                     additionalProperties = false
                  }
               }
            }
         }
      }
   }
)
