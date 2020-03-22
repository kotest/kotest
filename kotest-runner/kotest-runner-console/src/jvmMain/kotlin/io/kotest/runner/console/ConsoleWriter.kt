package io.kotest.runner.console

import io.kotest.core.engine.TestEngineListener

/**
 * An implementation of [TestEngineListener] that will listen to events in the Kotest engine,
 * and write those out to the console in a particular format.
 *
 * For example, see [TeamCityConsoleWriter] which will write out test events in a format that
 * Intellij parses and displays in it's test window.
 */
interface ConsoleWriter : TestEngineListener {

   /**
    * Returns true if at least one test has failed or errored during execution.
    */
   fun hasErrors(): Boolean
}
