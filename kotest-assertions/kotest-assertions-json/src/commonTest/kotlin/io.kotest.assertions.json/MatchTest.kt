@file:Suppress("DEPRECATION") // FIXME remove deprecation suppression when io.kotest.assertions.json.JsonMatchersKt.shouldMatchJson is removed

package io.kotest.assertions.json

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.shouldStartWith

class MatchTest : StringSpec() {

   private val json1 = """ { "name" : "sam", "location" : "london" } """
   private val json2 = """ { "location": "london", "name" : "sam" } """
   private val json3 = """ { "location": "chicago", "name" : "sam" } """

   init {
      "test json equality" {
         json1.shouldMatchJson(json2)
         json1.shouldNotMatchJson(json3)

         null.shouldMatchJson(null)
         null.shouldNotMatchJson(json1)
         json1.shouldNotMatchJson(null)

         shouldThrow<AssertionError> { null.shouldNotMatchJson(null) }
         shouldThrow<AssertionError> { null.shouldMatchJson(json1) }
         shouldThrow<AssertionError> { json1.shouldMatchJson(null) }
      }

      "test json equality throws with invalid actual json" {
         shouldThrow<AssertionError> {
            """
                  {
                    "merchant_or_brand": "BIGJIMS",
                    "input_source": "Scraper",
                    "source_url": "scraper:Test1.0",
                    "timestamp": "2019-10-30T06:02:03.000+00:00",
                    "item_sku": "123",
                    "product_sku": "789",
                    "name": "MY TRAINERS",
                    "brand": "NIKE",
                    "description": "Great pair of super cool sneakers",
                    "url": "https://www.ae.com/us/en/p/men/shoes/sneakers/aeo-knit-runner-shoe",
                    "status": "Active",
                    "attributes": [
                      {
                        "name": "gender",
                        "value": "Male",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "color",
                        "value": "blue",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "pattern",
                        "value": "striped",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "material",
                        "value": "sneaker material",
                        "type": "INTERNAL"
                      }
                    ]
                  }

                  {
                    "merchant_or_brand": "BIGJIMS",
                    "input_source": "Scraper",
                    "source_url": "scraper:Test1.0",
                    "timestamp": "2019-10-30T06:02:03.000+00:00",
                    "item_sku": "123",
                    "product_sku": "789",
                    "name": "MY TRAINERS",
                    "brand": "NIKE",
                    "description": "Great pair of super cool sneakers",
                    "url": "https://www.ae.com/us/en/p/men/shoes/sneakers/aeo-knit-runner-shoe",
                    "status": "Active",
                    "attributes": [
                      {
                        "name": "gender",
                        "value": "Male",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "color",
                        "value": "blue",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "pattern",
                        "value": "striped",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "material",
                        "value": "sneaker material",
                        "type": "INTERNAL"
                      }
                    ]
                  }
            """.shouldMatchJson(
               """
                 {
                    "merchant_or_brand": "BIGJIMS",
                    "input_source": "Scraper",
                    "source_url": "scraper:Test1.0",
                    "timestamp": "2019-10-30T06:02:03.000+00:00",
                    "item_sku": "123",
                    "product_sku": "789",
                    "name": "MY TRAINERS",
                    "brand": "NIKE",
                    "description": "Great pair of super cool sneakers",
                    "url": "https://www.ae.com/us/en/p/men/shoes/sneakers/aeo-knit-runner-shoe",
                    "status": "Active",
                    "attributes": [
                      {
                        "name": "gender",
                        "value": "Male",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "color",
                        "value": "blue",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "pattern",
                        "value": "striped",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "material",
                        "value": "sneaker material",
                        "type": "INTERNAL"
                      }
                    ]
                  }
            """
            )
         }.message shouldStartWith "expected: actual json to be valid json: "

      }

      @Suppress("JsonStandardCompliance") // invalid JSON is desired in this test
      "test json equality throws with invalid expected json" {
         shouldThrow<AssertionError> {
            """
                 {
                    "merchant_or_brand": "BIGJIMS",
                    "input_source": "Scraper",
                    "source_url": "scraper:Test1.0",
                    "timestamp": "2019-10-30T06:02:03.000+00:00",
                    "item_sku": "123",
                    "product_sku": "789",
                    "name": "MY TRAINERS",
                    "brand": "NIKE",
                    "description": "Great pair of super cool sneakers",
                    "url": "https://www.ae.com/us/en/p/men/shoes/sneakers/aeo-knit-runner-shoe",
                    "status": "Active",
                    "attributes": [
                      {
                        "name": "gender",
                        "value": "Male",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "color",
                        "value": "blue",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "pattern",
                        "value": "striped",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "material",
                        "value": "sneaker material",
                        "type": "INTERNAL"
                      }
                    ]
                  }
            """.shouldMatchJson(
               """
                  {
                    "merchant_or_brand": "BIGJIMS",
                    "input_source": "Scraper",
                    "source_url": "scraper:Test1.0",
                    "timestamp": "2019-10-30T06:02:03.000+00:00",
                    "item_sku": "123",
                    "product_sku": "789",
                    "name": "MY TRAINERS",
                    "brand": "NIKE",
                    "description": "Great pair of super cool sneakers",
                    "url": "https://www.ae.com/us/en/p/men/shoes/sneakers/aeo-knit-runner-shoe",
                    "status": "Active",
                    "attributes": [
                      {
                        "name": "gender",
                        "value": "Male",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "color",
                        "value": "blue",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "pattern",
                        "value": "striped",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "material",
                        "value": "sneaker material",
                        "type": "INTERNAL"
                      }
                    ]
                  }

                  {
                    "merchant_or_brand": "BIGJIMS",
                    "input_source": "Scraper",
                    "source_url": "scraper:Test1.0",
                    "timestamp": "2019-10-30T06:02:03.000+00:00",
                    "item_sku": "123",
                    "product_sku": "789",
                    "name": "MY TRAINERS",
                    "brand": "NIKE",
                    "description": "Great pair of super cool sneakers",
                    "url": "https://www.ae.com/us/en/p/men/shoes/sneakers/aeo-knit-runner-shoe",
                    "status": "Active",
                    "attributes": [
                      {
                        "name": "gender",
                        "value": "Male",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "color",
                        "value": "blue",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "pattern",
                        "value": "striped",
                        "type": "INTERNAL"
                      },
                      {
                        "name": "material",
                        "value": "sneaker material",
                        "type": "INTERNAL"
                      }
                    ]
                  }
            """.trimIndent()
            )
         }.message shouldStartWith "expected: expected json to be valid json: "

      }
   }
}
