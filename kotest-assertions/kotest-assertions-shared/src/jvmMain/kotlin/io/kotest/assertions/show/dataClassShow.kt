package io.kotest.assertions.show

actual fun <A : Any> dataClassShow(): Show<A> = object : Show<A> {
   override fun show(a: A): Printed {
      require(a::class.isData) { "This instance of the Show typeclass only supports data classes" }
      return Printed(a.toString())
//    return "${a::class.simpleName}(\n" +
//      a::class.memberProperties.joinToString("\n") {
//        "- ${it.name}: ${it.getter.call(a)}"
//      } + "\n)"
//  }
   }
}
