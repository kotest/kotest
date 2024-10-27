package io.kotest.assertions.json.paths

import io.kotest.assertions.json.beJsonArray
import io.kotest.assertions.json.beJsonObject
import io.kotest.assertions.json.beValidJson
import io.kotest.assertions.json.matchJson
import io.kotest.matchers.and
import io.kotest.matchers.paths.aFile
import io.kotest.matchers.paths.exist
import io.kotest.matchers.should
import java.nio.file.Path
import kotlin.io.path.readText

fun Path.shouldBeEmptyJsonArray(): Path {
   this should (exist() and aFile() and matchJson("[]").contramap { it.readText() })
   return this
}

fun Path.shouldBeEmptyJsonObject(): Path {
   this should (exist() and aFile() and matchJson("{}").contramap { it.readText() })
   return this
}

fun Path.shouldBeJsonArray(): Path {
   this should (exist() and aFile() and beJsonArray().contramap { it.readText() })
   return this
}

fun Path.shouldNotBeJsonArray(): Path {
   this should (exist() and aFile() and beJsonArray().contramap<Path> { it.readText() }.invert())
   return this
}

fun Path.shouldBeJsonObject(): Path {
   this should (exist() and aFile() and beJsonObject().contramap { it.readText() })
   return this
}

fun Path.shouldNotBeJsonObject(): Path {
   this should (exist() and aFile() and beJsonObject().contramap<Path> { it.readText() }.invert())
   return this
}

fun Path.shouldBeValidJson(): Path {
   this should (exist() and aFile() and beValidJson().contramap { it.readText() })
   return this
}

fun Path.shouldNotBeValidJson(): Path {
   this should (exist() and aFile() and beValidJson().contramap<Path> { it.readText() }.invert())
   return this
}
