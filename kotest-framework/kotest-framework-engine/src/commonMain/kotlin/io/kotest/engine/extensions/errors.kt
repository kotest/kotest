package io.kotest.engine.extensions

import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener

sealed class ExtensionException(val t: Throwable) : Exception(t) {
   class BeforeSpecException(cause: Throwable) : ExtensionException(cause)
   class AfterSpecException(cause: Throwable) : ExtensionException(cause)

   class AfterProjectException(cause: Throwable, val listener: AfterProjectListener) : ExtensionException(cause)
   class BeforeProjectException(cause: Throwable, val listener: BeforeProjectListener) : ExtensionException(cause)

   class BeforeInvocationException(cause: Throwable) : ExtensionException(cause)
   class AfterInvocationException(cause: Throwable) : ExtensionException(cause)
   class BeforeEachException(cause: Throwable) : ExtensionException(cause)
   class AfterEachException(cause: Throwable) : ExtensionException(cause)
   class BeforeContainerException(cause: Throwable) : ExtensionException(cause)
   class AfterContainerException(cause: Throwable) : ExtensionException(cause)
   class BeforeAnyException(cause: Throwable) : ExtensionException(cause)
   class AfterAnyException(cause: Throwable) : ExtensionException(cause)
   class PrepareSpecException(cause: Throwable) : ExtensionException(cause)
   class FinalizeSpecException(cause: Throwable) : ExtensionException(cause)
   class IgnoredSpecException(cause: Throwable) : ExtensionException(cause)
}

internal class MultipleExceptions(val causes: List<Throwable>) : Exception(causes.first())
