package io.kotest.engine.teamcity

/**
 * Escapes strings for use when writing team city service messages.
 * For escaped values, TeamCity uses a vertical bar | as an escape character
 *
 * @see [Teamcity docs](https://www.jetbrains.com/help/teamcity/service-messages.html#Escaped+values)
 * @see https://www.jetbrains.com/help/teamcity/6.5/build-script-interaction-with-teamcity.html?Build%2BScript%2BInteraction%2Bwith%2BTeamCity#BuildScriptInteractionwithTeamCity-servMsgsServiceMessages
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
