package io.kotest.framework.teamcity

class TeamCityMessageParser {

   private val prefix: String = "##teamcity"
   private val regex = "$prefix\\[(.+?)(\\s.*)?]".toRegex()
   private val propRegex = "(.+?)='(.+?)'".toRegex()

   fun parse(input: String): TeamCityMessage? {
      val match = regex.matchEntire(input.trim()) ?: return null
      val properties = match.groupValues.getOrNull(2) ?: ""
      return TeamCityMessage(
         type = TeamCityMessageType.fromWireName(match.groupValues[1].trim()),
         properties = propRegex.findAll(properties).associate {
            val name = it.groupValues[1].trim()
            val value = it.groupValues[2].trim()
            Pair(name, value)
         }
      )
   }
}

data class TeamCityMessage(
   val type: TeamCityMessageType,
   val properties: Map<String, String>,
) {
   operator fun get(name: String): String? = properties[name]
}

enum class TeamCityMessageType(private val wireName: String) {
   TestSuiteStarted("testSuiteStarted"),
   TestSuiteFinished("testSuiteFinished"),
   TestStarted("testStarted"),
   TestFinished("testFinished");

   companion object {
      fun fromWireName(input: String): TeamCityMessageType {
         return TeamCityMessageType.entries.first { it.wireName == input }
      }
   }
}
