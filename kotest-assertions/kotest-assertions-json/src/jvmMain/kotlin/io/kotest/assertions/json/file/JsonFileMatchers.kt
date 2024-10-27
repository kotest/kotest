package io.kotest.assertions.json.file

import io.kotest.assertions.json.beJsonArray
import io.kotest.assertions.json.beJsonObject
import io.kotest.assertions.json.beValidJson
import io.kotest.assertions.json.matchJson
import io.kotest.matchers.and
import io.kotest.matchers.file.aFile
import io.kotest.matchers.file.exist
import io.kotest.matchers.should
import java.io.File

fun File.shouldBeEmptyJsonArray(): File {
   this should (exist() and aFile() and matchJson("[]").contramap { it.readText() })
   return this
}

fun File.shouldBeEmptyJsonObject(): File {
   this should (exist() and aFile() and matchJson("{}").contramap { it.readText() })
   return this
}

fun File.shouldBeJsonArray(): File {
   this should (exist() and aFile() and beJsonArray().contramap { it.readText() })
   return this
}

fun File.shouldNotBeJsonArray(): File {
   this should (exist() and aFile() and beJsonArray().contramap<File> { it.readText() }.invert())
   return this
}

fun File.shouldBeJsonObject(): File {
   this should (exist() and aFile() and beJsonObject().contramap { it.readText() })
   return this
}

fun File.shouldNotBeJsonObject(): File {
   this should (exist() and aFile() and beJsonObject().contramap<File> { it.readText() }.invert())
   return this
}

fun File.shouldBeValidJson(): File {
   this should (exist() and aFile() and beValidJson().contramap { it.readText() })
   return this
}

fun File.shouldNotBeValidJson(): File {
   this should (exist() and aFile() and beValidJson().contramap<File> { it.readText() }.invert())
   return this
}
