package io.kotest.assertions.print

object MapPrint : Print<Map<*, *>> {

   override fun print(a: Map<*, *>, level: Int): Printed {
      return Printed(a.map { (k, v) -> recursiveRepr(a, k, level).value to recursiveRepr(a, v, level).value }
         .toString())
   }

   @Deprecated(PRINT_DEPRECATION_MSG)
   override fun print(a: Map<*, *>): Printed = print(a, 0)
}
