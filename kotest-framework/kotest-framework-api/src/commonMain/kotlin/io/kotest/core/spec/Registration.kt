package io.kotest.core.spec

import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

interface Registration {

   /**
    * Registers the given [NestedTest] and returns the [TestResult] if the test
    * was immediately executed. If the test was deferred for execution later,
    * then this function will return null.
    */
   suspend fun runNestedTestCase(parent: TestCase, nested: NestedTest): TestResult?
}

data class RegistrationContextElement(val registration: Registration) :
   AbstractCoroutineContextElement(RegistrationContextElement) {
   companion object Key : CoroutineContext.Key<RegistrationContextElement>
}

val CoroutineContext.registration: Registration
   get() = get(RegistrationContextElement)?.registration
      ?: error("registration is not injected into this CoroutineContext")
