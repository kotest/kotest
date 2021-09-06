package io.kotest.engine

import io.kotest.mpp.log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

/**
 * Launch the [TestEngine] created from this builder inside a Javascript [kotlin.js.Promise].
 */
fun TestEngineLauncher.promise() {
   log { "TestEngineLauncher: Launching Test Engine in blocking mode" }
   GlobalScope.promise {
      val engine = TestEngine(toConfig())
      engine.execute(testSuite())
   }
}
