package io.kotest.assertions.jsoup

import java.io.InputStreamReader

object ResourceLoader {
   fun getFileAsString(relPath: String): String {
      val fileStream = javaClass.classLoader.getResourceAsStream(relPath)
      val fileReader: InputStreamReader? = fileStream.reader()
      return fileReader?.readText() ?: "// error: file not found or null"
   }
}
