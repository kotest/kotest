package io.kotest.assertions.print

class IterablePrint<T> : Print<Iterable<T>> {
   override fun print(a: Iterable<T>): Printed = ListPrint<T>().print(a.toList())
}

