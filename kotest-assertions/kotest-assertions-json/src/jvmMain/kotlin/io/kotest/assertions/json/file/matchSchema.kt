package io.kotest.assertions.json.file

import io.kotest.assertions.json.paths.shouldMatchSchema
import io.kotest.assertions.json.paths.shouldNotMatchSchema
import io.kotest.assertions.json.schema.JsonSchema
import io.kotest.common.ExperimentalKotest
import java.io.File

@ExperimentalKotest
infix fun File.shouldMatchSchema(schema: JsonSchema): File {
   this.toPath().shouldMatchSchema(schema)
   return this
}

@ExperimentalKotest
infix fun File.shouldNotMatchSchema(schema: JsonSchema): File {
   this.toPath().shouldNotMatchSchema(schema)
   return this
}
