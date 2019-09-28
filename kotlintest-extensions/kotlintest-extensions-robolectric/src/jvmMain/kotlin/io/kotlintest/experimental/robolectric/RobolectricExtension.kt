package io.kotlintest.experimental.robolectric

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.ConstructorExtension
import io.kotlintest.extensions.TestCaseExtension
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

class RobolectricExtension : ConstructorExtension, TestCaseExtension {

    private val containedRobolectricRunner = ContainedRobolectricRunner()

    override fun <T : Spec> instantiate(clazz: KClass<T>): Spec? {
        if(clazz.isRobolectricClass()) return null
        return containedRobolectricRunner.sdkEnvironment.bootstrappedClass<Spec>(clazz.java).newInstance()
    }

    private fun <T : Spec> KClass<T>.isRobolectricClass() =
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