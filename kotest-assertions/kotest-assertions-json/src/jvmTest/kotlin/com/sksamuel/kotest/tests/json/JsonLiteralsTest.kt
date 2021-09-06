package com.sksamuel.kotest.tests.json

import io.kotest.core.spec.style.FunSpec
import jsonLiteralTests

class JsonLiteralsTest : FunSpec(
   {
      include(jsonLiteralTests())
   }
)
