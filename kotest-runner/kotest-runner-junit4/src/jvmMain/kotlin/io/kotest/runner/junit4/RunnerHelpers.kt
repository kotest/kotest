package io.kotest.runner.junit4

import io.kotest.TestCase
import org.junit.runner.Description

fun describeTestCase(testCase: TestCase): Description =
    Description.createTestDescription(testCase.spec.javaClass, testCase.description.tail().fullName())