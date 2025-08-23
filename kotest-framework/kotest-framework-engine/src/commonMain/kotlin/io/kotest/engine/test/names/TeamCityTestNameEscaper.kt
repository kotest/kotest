package io.kotest.engine.test.names

/**
 * Escapes/fixes test names that break in TeamCity formats.
 *
 * See:
 * https://www.jetbrains.com/help/teamcity/service-messages.html#Interpreting+Test+Names
 * https://github.com/kotest/kotest/issues/2302
 *
 * From the above:
 *
 * A full test name can have a form of: <suite name>: <package/namespace name>.<class name>.<test method>(<test parameters>),
 * where <class name> and <test method> cannot have dots in the names.
 * Only <test method> is a mandatory part of a full test name.
 *
 */
internal object TeamCityTestNameEscaper {
   fun escape(name: String): String {
      return name.replace(".", " ")
   }
}
