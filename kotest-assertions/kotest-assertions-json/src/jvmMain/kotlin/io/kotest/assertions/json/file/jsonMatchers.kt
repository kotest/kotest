package io.kotest.assertions.json.file

import io.kotest.assertions.json.paths.shouldBeEmptyJsonArray
import io.kotest.assertions.json.paths.shouldBeEmptyJsonObject
import io.kotest.assertions.json.paths.shouldBeJsonArray
import io.kotest.assertions.json.paths.shouldBeJsonObject
import io.kotest.assertions.json.paths.shouldBeValidJson
import io.kotest.assertions.json.paths.shouldNotBeJsonArray
import io.kotest.assertions.json.paths.shouldNotBeJsonObject
import io.kotest.assertions.json.paths.shouldNotBeValidJson
import kotlinx.serialization.json.Json
import java.io.File

fun File.shouldBeEmptyJsonArray(): File {
   this.toPath().shouldBeEmptyJsonArray()
   return this
}

fun File.shouldBeEmptyJsonObject(): File {
   this.toPath().shouldBeEmptyJsonObject()
   return this
}

fun File.shouldBeJsonArray(): File {
   this.toPath().shouldBeJsonArray()
   return this
}

fun File.shouldBeJsonArray(parser: Json): File {
   this.toPath().shouldBeJsonArray(parser)
   return this
}

fun File.shouldNotBeJsonArray(): File {
   this.toPath().shouldNotBeJsonArray()
   return this
}

fun File.shouldNotBeJsonArray(parser: Json): File {
   this.toPath().shouldNotBeJsonArray(parser)
   return this
}

fun File.shouldBeJsonObject(): File {
   this.toPath().shouldBeJsonObject()
   return this
}

fun File.shouldBeJsonObject(parser: Json): File {
   this.toPath().shouldBeJsonObject(parser)
   return this
}

fun File.shouldNotBeJsonObject(): File {
   this.toPath().shouldNotBeJsonObject()
   return this
}

fun File.shouldNotBeJsonObject(parser: Json): File {
   this.toPath().shouldNotBeJsonObject(parser)
   return this
}

fun File.shouldBeValidJson(): File {
   this.toPath().shouldBeValidJson()
   return this
}

fun File.shouldBeValidJson(parser: Json): File {
   this.toPath().shouldBeValidJson(parser)
   return this
}

fun File.shouldNotBeValidJson(): File {
   this.toPath().shouldNotBeValidJson()
   return this
}

fun File.shouldNotBeValidJson(parser: Json): File {
   this.toPath().shouldNotBeValidJson(parser)
   return this
}
