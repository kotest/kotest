package com.example.myapplication

import org.intellij.lang.annotations.Language

class InjectLanguageAnnotation {

   @Language("JSON")
   fun foo(value: String) = """
      {
         "foo": "$value"
      }
   """.trimIndent()
}
