package io.kotest.experimental.robolectric

import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.extensions.ConstructorExtension
import io.kotest.extensions.TestCaseExtension
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class RobolectricExtension : ConstructorExtension, TestCaseExtension {

    private val containedRobolectricRunner = ContainedRobolectricRunner()

    override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
        if (clazz.isNotRobolectricClass()) return null
        return containedRobolectricRunner.sdkEnvironment.bootstrappedClass<Spec>(clazz.java).newInstance()
    }

    private fun <T : Spec> KClass<T>.isNotRobolectricClass() =
        findAnnotation<RobolectricTest>() == null

    override suspend fun intercept(
        testCase: TestCase,
        execute: suspend (TestCase, suspend (TestResult) -> Unit) -> Unit,
        complete: suspend (TestResult) -> Unit
    ) {

        if (testCase.spec::class.isNotRobolectricClass()) {
            complete(TestResult.Ignored)
            return
        }

        val containedRobolectricRunner = ContainedRobolectricRunner()

        beforeTest(containedRobolectricRunner)
        execute(testCase) {
            complete(it)
        }
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
