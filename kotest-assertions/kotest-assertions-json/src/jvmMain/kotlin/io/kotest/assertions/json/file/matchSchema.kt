package io.kotest.assertions.json.file

import io.kotest.assertions.json.schema.JsonSchema
import io.kotest.assertions.json.schema.stringJsonMatcher
import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.and
import io.kotest.matchers.file.aFile
import io.kotest.matchers.file.exist
import io.kotest.matchers.should
import java.io.File

@ExperimentalKotest
infix fun File.shouldMatchSchema(schema: JsonSchema): File {
   this should (exist() and aFile() and stringJsonMatcher(schema).contramap { it.readText() })
   return this
}

@ExperimentalKotest
infix fun File.shouldNotMatchSchema(schema: JsonSchema): File {
   this should (exist() and aFile() and stringJsonMatcher(schema).contramap<File> { it.readText() }.invert())
   return this
}
