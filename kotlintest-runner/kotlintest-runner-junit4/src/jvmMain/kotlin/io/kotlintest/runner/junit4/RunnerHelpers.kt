package io.kotlintest.runner.junit4

import io.kotlintest.TestCase
import org.junit.runner.Description

fun describeTestCase(testCase: TestCase): Description =
    Description.createTestDescription(testCase.spec.javaClass, testCase.description.tail().fullName())