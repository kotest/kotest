package io.kotest

import io.kotest.core.TestCase

data class TestCaseContext(
   val spec: SpecClass,
   val case: TestCase
)
