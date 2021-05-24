package io.kotest.property.exhaustive

import io.kotest.property.Exhaustive
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path

/**
 * Returns an [Exhaustive] that enumerates all the lines in the given file.
 */
fun Exhaustive.Companion.lines(file: File, charset: Charset = Charsets.UTF_8): Exhaustive<String> {
   val contents = file.readLines(charset)
   return exhaustive(contents)
}

/**
 * Returns an [Exhaustive] that enumerates all the lines in the given file.
 */
fun Exhaustive.Companion.lines(file: Path, charset: Charset = Charsets.UTF_8) = lines(file.toFile(), charset)
