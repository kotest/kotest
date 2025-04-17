package io.kotest.assertions.json.paths

import io.kotest.assertions.json.schema.JsonSchema
import io.kotest.assertions.json.schema.stringJsonMatcher
import io.kotest.common.ExperimentalKotest
import io.kotest.matchers.and
import io.kotest.matchers.should
import java.nio.file.Path
import kotlin.io.path.readText
import io.kotest.matchers.paths.aFile
import io.kotest.matchers.paths.exist

@ExperimentalKotest
infix fun Path.shouldMatchSchema(schema: JsonSchema): Path {
   this should (exist() and aFile() and stringJsonMatcher(schema).contramap { it.readText() })
   return this
}

@ExperimentalKotest
infix fun Path.shouldNotMatchSchema(schema: JsonSchema): Path {
   this should (exist() and aFile() and stringJsonMatcher(schema).contramap<Path> { it.readText() }.invert())
   return this
}
