package io.kotest.assertions.json

import io.kotest.core.spec.style.FunSpec
import jsonLiteralTests

class JsonLiteralsTest : FunSpec(
   {
      include(jsonLiteralTests())
   }
)
