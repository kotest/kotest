package io.kotest.core.listeners

class AfterProjectListenerException : RuntimeException {
   constructor(message: String, t: Throwable?, name: String) : super(message, t) {
      this.name = name
   }

   constructor(message: String, name: String) : super(message) {
      this.name = name
   }

   constructor(name: String, t: Throwable) : super(t) {
      this.name = name
   }

   val name: String
}

class BeforeProjectListenerException : RuntimeException {
   constructor(message: String, t: Throwable?, name: String) : super(message, t) {
      this.name = name
   }

   constructor(message: String, name: String) : super(message) {
      this.name = name
   }

   constructor(name: String, t: Throwable) : super(t) {
      this.name = name
   }

   val name: String
}
