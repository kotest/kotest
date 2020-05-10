@file:JvmName("platformjvm")
package io.kotest.assertions.show

import java.nio.file.Path

actual fun <A : Any> platformShow(a: A): Show<A>? = when (a) {
   is Path -> PathShow as Show<A>
   else -> null
}
