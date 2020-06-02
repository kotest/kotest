package io.kotest.assertions.show

// native doesn't support full reflection yet so we just need to delegate to the instances toString
actual fun <A : Any> dataClassShow(): Show<A> = DefaultShow
