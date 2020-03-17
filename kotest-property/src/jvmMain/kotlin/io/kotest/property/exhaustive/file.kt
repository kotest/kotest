package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive
import java.io.File
import java.nio.charset.Charset

fun Exhaustive.Companion.lines(file: File, charset: Charset = Charsets.UTF_8): Exhaustive<String> {
   val contents = file.readLines(charset)
   return exhaustive(contents)
}
