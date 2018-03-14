package io.kotlintest.mock

import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing

inline fun <reified T> mock() = Mockito.mock(T::class.java)

inline fun <reified T : Any> spy() = Mockito.spy(T::class.java)

fun <A> `when`(methodCall: A): OngoingStubbing<A> = Mockito.`when`(methodCall)

fun <T> any(): T {
    Mockito.any<T>()
    return null as T
}
