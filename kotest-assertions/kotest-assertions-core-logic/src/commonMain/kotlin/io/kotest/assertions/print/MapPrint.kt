package io.kotest.assertions.print

object MapPrint : Print<Map<*, *>> {

   override fun print(a: Map<*, *>): Printed {
      return Printed(a.map { (k, v) ->
         recursiveRepr(a, k).value to recursiveRepr(a, v).value
      }.toString())
   }
}
