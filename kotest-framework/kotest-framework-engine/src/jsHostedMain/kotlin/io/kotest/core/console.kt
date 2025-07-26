package io.kotest.core

object console {
   fun log(message: Any?) = println(message as? String ?: "(null)")
}
