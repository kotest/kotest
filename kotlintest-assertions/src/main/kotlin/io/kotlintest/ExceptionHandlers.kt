package io.kotlintest

@Deprecated("This method was moved to another package. use the alternative instead",
        ReplaceWith("shouldThrowUnit(thunk)", "io.kotlintest.throwablehandling.shouldThrowUnit"))
inline fun <reified T : Throwable> shouldThrowUnit( thunk: () -> Unit): T = io.kotlintest.throwablehandling.shouldThrowUnit(thunk)

@Deprecated("This method was moved to another package. use the alternative instead",
        ReplaceWith("shouldThrow(thunk)", "io.kotlintest.throwablehandling.shouldThrow"))
inline fun <reified T : Throwable> shouldThrow(thunk: () -> Any?): T = io.kotlintest.throwablehandling.shouldThrow(thunk)


@Deprecated("This method was moved to another package. use the alternative instead",
        ReplaceWith("shouldThrowExactly(thunk)", "io.kotlintest.throwablehandling.shouldThrowExactly"))
inline fun <reified T : Throwable> shouldThrowExactly(thunk: () -> Any?): T = io.kotlintest.throwablehandling.shouldThrowExactly(thunk)

@Deprecated("This method was moved to another package. use the alternative instead",
        ReplaceWith("shouldThrowAny(thunk)", "io.kotlintest.throwablehandling.shouldThrowAny"))
inline fun shouldThrowAny(thunk: () -> Any?) = io.kotlintest.throwablehandling.shouldThrowAny(thunk)
