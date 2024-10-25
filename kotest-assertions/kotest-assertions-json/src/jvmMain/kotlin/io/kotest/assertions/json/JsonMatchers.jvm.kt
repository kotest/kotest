package io.kotest.assertions.json

import java.io.File
import java.nio.file.Path
import kotlin.io.path.readText

fun Path.shouldBeEmptyJsonArray(): Path {
   this.readText().shouldBeEmptyJsonArray()
   return this
}

fun File.shouldBeEmptyJsonArray(): File {
   this.readText().shouldBeEmptyJsonArray()
   return this
}

fun Path.shouldBeEmptyJsonObject(): Path {
   this.readText().shouldBeEmptyJsonObject()
   return this
}

fun File.shouldBeEmptyJsonObject(): File {
   this.readText().shouldBeEmptyJsonObject()
   return this
}

fun Path.shouldBeJsonArray(): Path {
   this.readText().shouldBeJsonArray()
   return this
}

fun File.shouldBeJsonArray(): File {
   this.readText().shouldBeJsonArray()
   return this
}

fun Path.shouldNotBeJsonArray(): Path {
   this.readText().shouldNotBeJsonArray()
   return this
}

fun File.shouldNotBeJsonArray(): File {
   this.readText().shouldNotBeJsonArray()
   return this
}

fun Path.shouldBeJsonObject(): Path {
   this.readText().shouldBeJsonObject()
   return this
}

fun File.shouldBeJsonObject(): File {
   this.readText().shouldBeJsonObject()
   return this
}

fun Path.shouldNotBeJsonObject(): Path {
   this.readText().shouldNotBeJsonObject()
   return this
}

fun File.shouldNotBeJsonObject(): File {
   this.readText().shouldNotBeJsonObject()
   return this
}

fun Path.shouldBeValidJson(): Path {
   this.readText().shouldBeValidJson()
   return this
}

fun File.shouldBeValidJson(): File {
   this.readText().shouldBeValidJson()
   return this
}

fun Path.shouldNotBeValidJson(): Path {
   this.readText().shouldNotBeValidJson()
   return this
}

fun File.shouldNotBeValidJson(): File {
   this.readText().shouldNotBeValidJson()
   return this
}
