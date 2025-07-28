package io.kotest.assertions.print

object MapPrint : Print<Map<*, *>> {

   override fun print(a: Map<*, *>, level: Int): Printed {
      return Printed(a.map { (k, v) ->
         recursiveRepr(a, k, level).value to recursiveRepr(a, v, level).value
      }.toString())
   }
}
