package io.kotest.engine.console

expect val consoleRenderer: ConsoleRenderer

interface ConsoleRenderer {

   fun println()
   fun println(str: String)
   fun print(str: String)

   fun bold(str: String) = str
   fun green(str: String) = str
   fun greenBold(str: String) = str
   fun red(str: String) = str
   fun brightRed(str: String) = str
   fun brightRedBold(str: String) = str
   fun redBold(str: String) = str
   fun yellow(str: String) = str
   fun brightYellow(str: String) = str
   fun brightYellowBold(str: String) = str
   fun yellowBold(str: String) = str
}

object NoopConsoleRenderer : ConsoleRenderer {
   override fun println() {}
   override fun println(str: String) {}
   override fun print(str: String) {}
   override fun green(str: String): String = str
}
