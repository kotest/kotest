package com.example.myapplication

import org.intellij.lang.annotations.Language

class InjectLanguageAnnotation {

   // Test for "Duplicate class org.intellij.lang.annotations.Language found"
   // https://github.com/kotest/kotest/issues/3387
   @Language("JSON")
   fun foo(value: String) = """
      {
         "foo": "$value"
      }
   """.trimIndent()
}
