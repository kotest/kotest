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
   override fun greenBold(str: String) = (TextColors.green + TextStyles.bold).invoke(str)
   override fun red(str: String) = TextColors.red(str)
   override fun redBold(str: String) = (TextColors.red + TextStyles.bold).invoke(str)
   override fun brightRed(str: String) = TextColors.brightRed(str)
   override fun brightRedBold(str: String) = (TextColors.brightRed + TextStyles.bold).invoke(str)
   override fun yellow(str: String) = TextColors.yellow(str)
   override fun yellowBold(str: String) = (TextColors.yellow + TextStyles.bold).invoke(str)
   override fun brightYellow(str: String) = TextColors.brightYellow(str)
   override fun brightYellowBold(str: String) = (TextColors.brightYellow + TextStyles.bold).invoke(str)
}
