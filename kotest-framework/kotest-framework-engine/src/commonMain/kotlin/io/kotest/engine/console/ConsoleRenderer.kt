package io.kotest.engine.console

expect val consoleRenderer: ConsoleRenderer

interface ConsoleRenderer {

   fun print(str: String)
   fun println()
   fun println(str: String)

   fun bold(str: String): String
   fun green(str: String): String
   fun greenBold(str: String): String
   fun red(str: String): String
   fun brightRed(str: String): String
   fun brightRedBold(str: String): String
   fun redBold(str: String): String
   fun yellow(str: String): String
   fun brightYellow(str: String): String
   fun brightYellowBold(str: String): String
   fun yellowBold(str: String): String
}
