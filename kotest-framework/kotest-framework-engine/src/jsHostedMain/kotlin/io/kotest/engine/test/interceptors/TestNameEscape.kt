package io.kotest.engine.test.interceptors

/**
 * Escapes/fixes test names that break on JS.
 * https://www.jetbrains.com/help/teamcity/service-messages.html#Interpreting+test+names
 * https://github.com/kotest/kotest/issues/2302
 */
fun testNameEscape(name: String): String {
   return name.replace('.', ' ')
}
