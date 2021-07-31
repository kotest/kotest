package io.kotest.engine.teamcity

object Escaper {
   fun escapeForTeamCity(str: String): String = StringBuilder(str.length).apply {
      for (char in str) {
         append(
            when (char) {
               '|' -> "||"
               '\'' -> "|'"
               '\n' -> "|n"
               '\r' -> "|r"
               '[' -> "|["
               ']' -> "|]"
               else -> char
            }
         )
      }
   }.toString()
}
