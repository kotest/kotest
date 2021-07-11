package io.kotest.engine.execution

internal class TeamCityLogger {

   private fun String.escapeForTC(): String = StringBuilder(length).apply {
      for (char in this@escapeForTC) {
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

   private fun finish(testCase: String, durationMs: Long) =
      report("testFinished name='${testCase}' duration='$durationMs'")

   private fun report(msg: String) = println("##teamcity[$msg]")

   fun start(testCase: String) = report(
      "testStarted" +
         " name='${testCase}'" +
         " locationHint='ktest:test://${testCase}.${testCase}'"
   )

   fun startSuite(suite: String) = report(
      "testSuiteStarted" +
         " name='${suite}'" +
         " locationHint='kotest:suite://${suite}'"
   )

   fun finishSuite(suite: String, timeMillis: Long) = report("testSuiteFinished name='${suite}'")

   fun pass(testCase: String, timeMillis: Long) = finish(testCase, timeMillis)

   fun fail(testCase: String, e: Throwable, timeMillis: Long) {
//      val stackTrace = e.dumpStackTrace().escapeForTC()
      val message = e.message?.escapeForTC()
//      report("testFailed name='${testCase}' message='$message' details='$stackTrace'")
      finish(testCase, timeMillis)
   }
}
