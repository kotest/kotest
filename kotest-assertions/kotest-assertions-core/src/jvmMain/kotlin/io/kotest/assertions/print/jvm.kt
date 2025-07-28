package io.kotest.assertions.print

import java.io.File
import java.nio.file.Path

object FilePrint : Print<File> {
   override fun print(a: File, level: Int): Printed = Printed((indent(level) + a.path), File::class)
}

object PathPrint : Print<Path> {
   override fun print(a: Path, level: Int): Printed = Printed(a.toString(), Path::class)
}

object StringBuilderPrint : Print<StringBuilder> {
   override fun print(a: StringBuilder, level: Int): Printed = Printed(a.toString(), StringBuilder::class)
}

object BigIntegerPrint : Print<java.math.BigInteger> {
   override fun print(a: java.math.BigInteger, level: Int): Printed = Printed(a.toString(), java.math.BigInteger::class)
}

object BigDecimalPrint : Print<java.math.BigDecimal> {
   override fun print(a: java.math.BigDecimal, level: Int): Printed = Printed(a.toString(), java.math.BigDecimal::class)
}
