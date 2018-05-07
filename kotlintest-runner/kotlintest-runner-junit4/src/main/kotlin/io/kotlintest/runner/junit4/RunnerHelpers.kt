package io.kotlintest.runner.junit4

import io.kotlintest.Spec
import io.kotlintest.TestCase
import org.junit.runner.Description

internal fun describeSpec(spec: Spec): Description =
    Description.createSuiteDescription(spec::class.java)

fun describeSpec(testCase: TestCase): Description =
    Description.createTestDescription(testCase.spec.javaClass, testCase.description.fullName())