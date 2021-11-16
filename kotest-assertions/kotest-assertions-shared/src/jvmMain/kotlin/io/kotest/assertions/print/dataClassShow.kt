package io.kotest.assertions.print

import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

private fun <A : Any> safePrint(a: A): String {
   return "${a::class.simpleName}"
}

private fun <A : Any> recursiveSafePrint(a: A, depth: Int): String {
   return if (depth > 0) {
      a::class.memberProperties
         .filter { it.isAccessible }
         .map {
            val value = try {
               it.getter.call(a)
            } catch (ex: Exception) {
               "unknown"
            }

            Pair(it.name, value)
         }
//         .filterNot { (_, it) -> seen.contains(it.hashCode()) }
         .joinToString(prefix = "${a::class.simpleName}(\n", postfix = "\n") { (name, value) ->
            if (value == null)
               "$name=null"
            else if (value::class.isData)
               "$name=${recursiveSafePrint(value, depth - 1)}"
            else
               "$name=unknown"
         }
   } else "${a::class.simpleName} - recursive reference detected"
}

actual fun <A : Any> dataClassPrint(): Print<A> = object : Print<A> {
   override fun print(a: A): Printed {
      require(a::class.isData) { "This instance of the Show typeclass only supports data classes" }
//      return Printed(a.toString())
      return Printed(recursiveSafePrint(a, 5))
//    return "${a::class.simpleName}(\n" +
//      a::class.memberProperties.joinToString("\n") {
//        "- ${it.name}: ${it.getter.call(a)}"
//      } + "\n)"
//  }
   }
}
