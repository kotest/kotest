package io.kotest.engine.test.scopes

import io.kotest.core.test.NestedTest
import io.kotest.engine.test.TestResult
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

data class RegistrationContextElement(val registration: Registration) :
   AbstractCoroutineContextElement(RegistrationContextElement) {
   companion object Key : CoroutineContext.Key<RegistrationContextElement>
}

interface Registration {
   suspend fun runNestedTestCase(nested: NestedTest): TestResult?
}

val CoroutineContext.registration: Registration
   get() = get(RegistrationContextElement)?.registration
      ?: error("registration is not injected into this CoroutineContext")
