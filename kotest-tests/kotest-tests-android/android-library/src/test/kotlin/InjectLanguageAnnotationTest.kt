package com.example.myapplication

import io.kotest.assertions.json.shouldMatchJson
import io.kotest.core.spec.style.FunSpec

class InjectLanguageAnnotationTest : FunSpec({
   test("foo") {
      InjectLanguageAnnotation().foo("hello").shouldMatchJson(
         """
             {
                "foo": "hello"
             }
          """.trimIndent()
      )
   }
})
