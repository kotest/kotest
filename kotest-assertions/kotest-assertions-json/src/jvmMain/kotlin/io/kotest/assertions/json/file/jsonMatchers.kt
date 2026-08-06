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

@IgnorableReturnValue
fun File.shouldBeEmptyJsonArray(): File {
   this.toPath().shouldBeEmptyJsonArray()
   return this
}

@IgnorableReturnValue
fun File.shouldBeEmptyJsonObject(): File {
   this.toPath().shouldBeEmptyJsonObject()
   return this
}

@IgnorableReturnValue
fun File.shouldBeJsonArray(): File {
   this.toPath().shouldBeJsonArray()
   return this
}

@IgnorableReturnValue
fun File.shouldBeJsonArray(parser: Json): File {
   this.toPath().shouldBeJsonArray(parser)
   return this
}

@IgnorableReturnValue
fun File.shouldNotBeJsonArray(): File {
   this.toPath().shouldNotBeJsonArray()
   return this
}

@IgnorableReturnValue
fun File.shouldNotBeJsonArray(parser: Json): File {
   this.toPath().shouldNotBeJsonArray(parser)
   return this
}

@IgnorableReturnValue
fun File.shouldBeJsonObject(): File {
   this.toPath().shouldBeJsonObject()
   return this
}

@IgnorableReturnValue
fun File.shouldBeJsonObject(parser: Json): File {
   this.toPath().shouldBeJsonObject(parser)
   return this
}

@IgnorableReturnValue
fun File.shouldNotBeJsonObject(): File {
   this.toPath().shouldNotBeJsonObject()
   return this
}

@IgnorableReturnValue
fun File.shouldNotBeJsonObject(parser: Json): File {
   this.toPath().shouldNotBeJsonObject(parser)
   return this
}

@IgnorableReturnValue
fun File.shouldBeValidJson(): File {
   this.toPath().shouldBeValidJson()
   return this
}

@IgnorableReturnValue
fun File.shouldBeValidJson(parser: Json): File {
   this.toPath().shouldBeValidJson(parser)
   return this
}

@IgnorableReturnValue
fun File.shouldNotBeValidJson(): File {
   this.toPath().shouldNotBeValidJson()
   return this
}

@IgnorableReturnValue
fun File.shouldNotBeValidJson(parser: Json): File {
   this.toPath().shouldNotBeValidJson(parser)
   return this
}
