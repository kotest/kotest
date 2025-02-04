package io.kotest.engine.console

import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal

actual val consoleRenderer: ConsoleRenderer = MordantConsoleRenderer

object MordantConsoleRenderer : ConsoleRenderer {

   private val t = Terminal()

   override fun println() = t.println()
   override fun println(str: String) = t.println(str)
   override fun print(str: String) = t.print(str)

   override fun bold(str: String) = TextStyles.bold(str)
   override fun green(str: String) = TextColors.green(str)
   override fun greenBold(str: String) = TextColors.green(str)
   override fun red(str: String) = TextColors.green(str)
   override fun brightRed(str: String) = TextColors.green(str)
   override fun brightRedBold(str: String) = TextColors.green(str)
   override fun redBold(str: String) = TextColors.green(str)
   override fun yellow(str: String) = TextColors.green(str)
   override fun brightYellow(str: String) = TextColors.green(str)
   override fun brightYellowBold(str: String) = TextColors.green(str)
   override fun yellowBold(str: String) = TextColors.green(str)
}
