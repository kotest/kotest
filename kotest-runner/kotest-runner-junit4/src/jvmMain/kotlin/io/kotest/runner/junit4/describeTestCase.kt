package io.kotest.runner.junit4

import io.kotest.core.test.TestCase
import org.junit.runner.Description

fun describeTestCase(testCase: TestCase): Description =
   Description.createTestDescription(
      testCase.spec::class.java,
      testCase.description.testDisplayPath().value
   )
