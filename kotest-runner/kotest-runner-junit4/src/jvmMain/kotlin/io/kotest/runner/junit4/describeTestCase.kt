package io.kotest.runner.junit4

import io.kotest.core.test.TestCase
import org.junit.runner.Description

fun describeTestCase(testCase: TestCase, displayName: String): Description =
   Description.createTestDescription(
      testCase.spec::class.java,
      displayName
   )
