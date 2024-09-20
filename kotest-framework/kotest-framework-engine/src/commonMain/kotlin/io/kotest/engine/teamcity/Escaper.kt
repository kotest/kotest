package io.kotest.engine.teamcity

/**
 * Escapes strings for use when writing team city service messages
 *
 * See [Teamcity docs](https://www.jetbrains.com/help/teamcity/service-messages.html#Escaped+values)
 */
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
               '\u2028' -> "|l"
               '\u0085' -> "|x"
               '\u2029' -> "|p"
               else -> char
            }
         )
      }
   }.toString()
}
