package io.kotlintest.runner.junit4

import io.kotlintest.Spec
import io.kotlintest.TestScope
import org.junit.runner.Description

internal fun describeScope(spec: Spec): Description =
    Description.createSuiteDescription(spec::class.java)

private fun describeTestContainer(scope: TestScope): Description =
    Description.createTestDescription(scope.spec.javaClass, scope.description.fullName())