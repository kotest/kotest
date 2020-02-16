package io.kotest.matchers.functions

private fun <R> (() -> R).shouldBeInvoked(test: (() -> R) -> Unit): R {

   val receiver = this
   var invoked = false
   var r: R? = null

   val wrapper: () -> R = {
      receiver.invoke().apply {
         invoked = true
         r = this
      }
   }

   test(wrapper)

   if (!invoked)
      throw AssertionError("Function0 was not invoked")

   return r!!
}

private fun <A, R> ((A) -> R).shouldBeInvoked(test: ((A) -> R) -> Unit): R {

   val receiver = this
   var invoked = false
   var r: R? = null

   val wrapper: (A) -> R = { a ->
      receiver.invoke(a).apply {
         invoked = true
         r = this
      }
   }

   test(wrapper)

   if (!invoked)
      throw AssertionError("Function1 was not invoked")

   return r!!
}

private fun <A, B, R> ((A, B) -> R).shouldBeInvoked(test: ((A, B) -> R) -> Unit): R {

   val receiver = this
   var invoked = false
   var r: R? = null

   val wrapper: (A, B) -> R = { a, b ->
      receiver.invoke(a, b).apply {
         invoked = true
         r = this
      }
   }

   test(wrapper)

   if (!invoked)
      throw AssertionError("Function1 was not invoked")

   return r!!
}

private fun <A, B, C, R> ((A, B, C) -> R).shouldBeInvoked(test: ((A, B, C) -> R) -> Unit): R {

   val receiver = this
   var invoked = false
   var r: R? = null

   val wrapper: (A, B, C) -> R = { a, b, c ->
      receiver.invoke(a, b, c).apply {
         invoked = true
         r = this
      }
   }

   test(wrapper)

   if (!invoked)
      throw AssertionError("Function1 was not invoked")

   return r!!
}
