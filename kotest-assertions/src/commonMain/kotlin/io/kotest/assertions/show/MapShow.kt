package io.kotest.assertions.show

object MapShow : Show<Map<*, *>> {
   override fun show(a: Map<*, *>): Printed {
      return Printed(a.map { (k, v) -> recursiveRepr(a, k).value to recursiveRepr(a, v).value }.toString())
   }
}
