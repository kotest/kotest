package io.kotest.experimental.robolectric

import org.junit.runners.model.FrameworkMethod
import org.robolectric.RobolectricTestRunner
import org.robolectric.internal.bytecode.InstrumentationConfiguration

class ContainedRobolectricRunner : RobolectricTestRunner(PlaceholderTest::class.java) {


    private val placeHolderMethod by lazy {
        children[0]
    }

    private val bootStrappedMethod by lazy {
        sdkEnvironment.bootstrappedClass<Any>(testClass.javaClass).getMethod(placeHolderMethod.name)

    }

    val sdkEnvironment by lazy {
        getSandbox(placeHolderMethod).also {
            configureSandbox(it, placeHolderMethod)
        }
    }

    fun containedBefore() {
        super.beforeTest(sdkEnvironment, placeHolderMethod, bootStrappedMethod)
    }

    fun containedAfter() {
        super.afterTest(placeHolderMethod, bootStrappedMethod)
        super.finallyAfterTest(placeHolderMethod)
    }

    override fun createClassLoaderConfig(method: FrameworkMethod?): InstrumentationConfiguration {
        return InstrumentationConfiguration.Builder(super.createClassLoaderConfig(method))
            .doNotAcquirePackage("io.kotest")
            .build()
    }

    class PlaceholderTest {
        @org.junit.Test
        fun testPlaceholder() {
        }
    }
}