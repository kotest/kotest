package io.kotest.engine.extensions

sealed class ExtensionException(val t: Throwable) : Exception(t) {
   class BeforeSpecException(cause: Throwable) : ExtensionException(cause)
   class AfterSpecException(cause: Throwable) : ExtensionException(cause)
   class AfterProjectException(cause: Throwable) : ExtensionException(cause)
   class BeforeProjectException(cause: Throwable) : ExtensionException(cause)
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

class MultipleExceptions(val causes: List<Throwable>) : Exception(causes.first())
