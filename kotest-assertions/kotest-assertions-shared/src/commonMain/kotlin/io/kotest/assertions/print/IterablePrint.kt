package io.kotest.assertions.print

class IterablePrint<T> : Print<Iterable<T>> {
   override fun print(a: Iterable<T>): Printed = print(a, 0)
   override fun print(a: Iterable<T>, level: Int): Printed = ListPrint<T>().print(a.toList(), level)
}

