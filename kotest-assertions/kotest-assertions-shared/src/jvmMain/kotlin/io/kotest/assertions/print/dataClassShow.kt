package io.kotest.assertions.print

actual fun <A : Any> dataClassPrint(): Print<A> = object : Print<A> {
   override fun print(a: A): Printed {
      require(a::class.isData) { "This instance of the Show typeclass only supports data classes" }
      return Printed(a.toString())
//    return "${a::class.simpleName}(\n" +
//      a::class.memberProperties.joinToString("\n") {
//        "- ${it.name}: ${it.getter.call(a)}"
//      } + "\n)"
//  }
   }
}
