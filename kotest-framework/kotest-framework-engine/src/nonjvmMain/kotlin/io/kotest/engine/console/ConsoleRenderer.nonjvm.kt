package io.kotest.engine.console

actual val consoleRenderer: ConsoleRenderer = PlainConsoleRenderer

/**
 * An implementation of [ConsoleRenderer] that does not do any formatting, and just outputs
 * to std out using [kotlin.io.print] and [kotlin.io.println].
 */
internal object PlainConsoleRenderer : ConsoleRenderer {

   override fun print(str: String) = kotlin.io.print(str)
   override fun println() = kotlin.io.println()
   override fun println(str: String) = kotlin.io.println(str)

   override fun bold(str: String) = str
   override fun green(str: String) = str
   override fun greenBold(str: String) = str
   override fun red(str: String) = str
   override fun brightRed(str: String) = str
   override fun brightRedBold(str: String) = str
   override fun redBold(str: String) = str
   override fun yellow(str: String) = str
   override fun brightYellow(str: String) = str
   override fun brightYellowBold(str: String) = str
   override fun yellowBold(str: String) = str
}
