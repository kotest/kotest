package io.kotest.assertions.print

import java.io.File
import java.nio.file.Path

object FilePrint : Print<File> {
   override fun print(a: File): Printed = Printed(a.path, File::class)
}

object PathPrint : Print<Path> {
   override fun print(a: Path): Printed = Printed(a.toString(), Path::class)
}

object StringBuilderPrint : Print<StringBuilder> {
   override fun print(a: StringBuilder): Printed = Printed(a.toString(), StringBuilder::class)
}

object BigIntegerPrint : Print<java.math.BigInteger> {
   override fun print(a: java.math.BigInteger): Printed = Printed(a.toString(), java.math.BigInteger::class)
}

object BigDecimalPrint : Print<java.math.BigDecimal> {
   override fun print(a: java.math.BigDecimal): Printed = Printed(a.toString(), java.math.BigDecimal::class)
}
