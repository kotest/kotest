package io.kotest.assertions.json.schema

import io.kotest.matchers.paths.shouldBeAFile
import io.kotest.matchers.paths.shouldExist
import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText

infix fun Path.shouldMatchSchema(schema: JsonSchema): Path {
   readText().shouldMatchSchema(schema)
   return this
}

infix fun File.shouldNotMatchSchema(schema: JsonSchema): File {
   readText().shouldNotMatchSchema(schema)
   return this
}
