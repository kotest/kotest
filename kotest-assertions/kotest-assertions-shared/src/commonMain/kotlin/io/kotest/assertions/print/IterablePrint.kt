package io.kotest.assertions.print

class IterablePrint<T> : Print<Iterable<T>> {
   @Deprecated("Use print(a, level) to respect level hints. Deprecated in 5.0.3", ReplaceWith("print(a, 0)"))
   override fun print(a: Iterable<T>): Printed = print(a, 0)
   override fun print(a: Iterable<T>, level: Int): Printed = ListPrint<T>().print(a.toList(), level)
}

