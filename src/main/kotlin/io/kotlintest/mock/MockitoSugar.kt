package io.kotlintest.mock

import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

inline fun <reified T> mock(): T {
  val klass = T::class
  val t = Mockito.mock(Class.forName(klass.qualifiedName)) as T
  return t
}

inline fun <reified T : Any> spy() = Mockito.spy(T::class.java)

fun <A> `when`(methodCall: A): OngoingStubbing<A> = Mockito.`when`(methodCall)
