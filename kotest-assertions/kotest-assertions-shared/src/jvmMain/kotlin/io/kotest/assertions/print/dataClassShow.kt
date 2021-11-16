package io.kotest.assertions.print

import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

private fun <A : Any> safePrint(a: A): String {
   return "${a::class.simpleName}"
}

private fun <A : Any> recursiveSafePrint(a: A, seen: MutableSet<Int>): String {
   return if (!seen.contains(a.hashCode())) {
      seen.add(a.hashCode())
      a::class.memberProperties
         .filter { it.isAccessible }
         .map { Pair(it.name, runCatching {
            it.getter.call(a)
         })}
         .filterNot { (_, it) -> if (it.isSuccess) { seen.contains(it.getOrNull()?.hashCode()) } else false }
         .joinToString(prefix = "${a::class.simpleName}(\n", postfix = "\n") { (name, value) ->
            value.fold(
               onFailure = {
                  "unknown"
               },
               onSuccess = {
                  if (it == null)
                     "$name=null"
                  else if (it::class.isData)
                     "$name=${recursiveSafePrint(it, seen)}"
                  else
                     "$name=unknown"
               }
            )
         }
   } else "${a::class.simpleName} - recursive reference detected"
}

actual fun <A : Any> dataClassPrint(): Print<A> = object : Print<A> {
   override fun print(a: A): Printed {
      require(a::class.isData) { "This instance of the Show typeclass only supports data classes" }
//      return Printed(a.toString())
      return Printed(recursiveSafePrint(a, mutableSetOf()))
//    return "${a::class.simpleName}(\n" +
//      a::class.memberProperties.joinToString("\n") {
//        "- ${it.name}: ${it.getter.call(a)}"
//      } + "\n)"
//  }
   }
}
