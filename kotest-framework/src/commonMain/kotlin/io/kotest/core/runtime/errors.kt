package io.kotest.core.runtime

class AfterProjectListenerException : RuntimeException {
   constructor(message: String, t: Throwable?) : super(message, t)
   constructor(message: String) : super(message)
   constructor(t: Throwable) : super(t)
}

class BeforeBeforeListenerException : RuntimeException {
   constructor(message: String, t: Throwable?) : super(message, t)
   constructor(message: String) : super(message)
   constructor(t: Throwable) : super(t)
}
