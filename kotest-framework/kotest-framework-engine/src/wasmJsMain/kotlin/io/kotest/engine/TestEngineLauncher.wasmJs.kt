package io.kotest.engine

import io.kotest.common.Platform
import io.kotest.engine.js.isJavascriptTestFrameworkAvailable
import io.kotest.engine.listener.ConsoleTestEngineListener
import io.kotest.engine.listener.TeamCityTestEngineListener
import io.kotest.engine.listener.TestEngineListener

internal actual fun Platform.listeners(): List<TestEngineListener> {
   return if (isJavascriptTestFrameworkAvailable()) {
      // if we have a javascript test framework available (eg running in the browser), then Kotest
      // will route test events using the framework functions, so we don't want to output using TCSM
      // todo these events don't appear in gradle output, might be a kotlin.test issue or something wrong in kotest
      // todo perhaps the kotlin.test is grabbing all stdout output in order to pick out the JS framework output
      listOf(ConsoleTestEngineListener())
   } else {
      // if we have no javascript test framework, eg running wasm inside NodeJS, then we will output TCSM messages
      // which the kotlin.test gradle plugin will pickup and process inside intellij.
      // For output when runing through gradle directly, we add the console listener
      // todo would be good to figure out if we're inside intellij or not and not include the console listener if so
      listOf(TeamCityTestEngineListener(), ConsoleTestEngineListener())
   }
}
