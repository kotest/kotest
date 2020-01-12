package io.kotest.experimental.robolectric

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.spec.SpecConfiguration
import io.kotest.extensions.ConstructorExtension
import io.kotest.extensions.TestCaseExtension
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class RobolectricExtension : ConstructorExtension, TestCaseExtension {

    private val containedRobolectricRunner = ContainedRobolectricRunner()

    override fun <T : SpecConfiguration> instantiate(clazz: KClass<T>): SpecConfiguration? {
        if(clazz.isRobolectricClass()) return null
        return containedRobolectricRunner.sdkEnvironment.bootstrappedClass<SpecConfiguration>(clazz.java).newInstance()
    }

    private fun <T : SpecConfiguration> KClass<T>.isRobolectricClass() =
        findAnnotation<RobolectricTest>() == null

    override suspend fun intercept(
       testCase: TestCase,
       execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
       complete: suspend (TestResult) -> Unit
    ) {
        if(testCase.spec::class.isRobolectricClass()) return super.intercept(testCase, execute, complete)

        val containedRobolectricRunner = ContainedRobolectricRunner()

        beforeTest(containedRobolectricRunner)
        execute(testCase) { complete(it) }
        afterTest(containedRobolectricRunner)
    }

    private fun beforeTest(containedRobolectricRunner: ContainedRobolectricRunner) {
        Thread.currentThread().contextClassLoader = containedRobolectricRunner.sdkEnvironment.robolectricClassLoader
        containedRobolectricRunner.containedBefore()
    }

    private fun afterTest(containedRobolectricRunner: ContainedRobolectricRunner) {
        containedRobolectricRunner.containedAfter()
        Thread.currentThread().contextClassLoader = RobolectricExtension::class.java.classLoader
    }

}

annotation class RobolectricTest
