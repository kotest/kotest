package io.kotest.assertions.show

import io.kotest.assertions.print.Print
import io.kotest.assertions.print.Printed

@Deprecated("Renamed to Print. This alias was deprecated since 5.0")
typealias Show<A> = Print<A>

@Deprecated("Moved packages. This alias was deprecated since 5.0")
typealias Printed = Printed
