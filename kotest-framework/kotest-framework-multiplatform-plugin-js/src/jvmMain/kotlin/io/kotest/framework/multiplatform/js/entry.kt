package io.kotest.framework.multiplatform.js

object EntryPoint {

   // we use a public val to register each spec
   //   const val LauncherValName = "kotest_launcher"

   // the method invoked on TestEngineLauncher to start the tests
   // in JS we use promise() which ultimately calls into GlobalScope.promise on JS platforms
   const val PromiseMethodName = "promise"

   // the FQN for the class used to launch the MPP engine
   const val TestEngineClassName = "io.kotest.engine.TestEngineLauncher"

   // the method invoked to add specs to the launcher, must exist on TestEngineLauncher
   const val WithSpecsMethodName = "withSpecs"

   // the method invoked to add configs on the launcher, must exist on TestEngineLauncher
   const val WithConfigMethodName = "withProjectConfig"
}
