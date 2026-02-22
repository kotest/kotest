package io.kotest.plugin.intellij.gradle

internal object VersionParser {

   fun parse(version: String): Version? {
      // we assume x.y.z.... format
      val tokens = version.split(".")
      if (tokens.size < 3) return null
      val major = tokens[0].take(2).toIntOrNull() ?: return null
      val minor = tokens[1].take(2).toIntOrNull() ?: return null
      val patch = tokens[2].take(2).toIntOrNull() ?: return null
      return Version(major, minor, patch)
   }
}

internal data class Version(val major: Int, val minor: Int, val patch: Int)
