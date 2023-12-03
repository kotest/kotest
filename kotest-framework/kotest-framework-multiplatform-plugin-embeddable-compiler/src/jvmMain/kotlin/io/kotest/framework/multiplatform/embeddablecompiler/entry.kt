package io.kotest.framework.multiplatform.embeddablecompiler

object EntryPoint {
   // we use a public val to register each spec
   const val LauncherValName = "launcher"

   // the method invoked to start the tests, must exist on TestEngineLauncher
   const val LaunchMethodName = "launch"

   // the method invoked on TestEngineLauncher to start the tests
   // in JS we use promise() which ultimately calls into GlobalScope.promise on JS platforms
   const val PromiseMethodName = "promise"

   /**
    * the FQN for the class used to launch the MPP engine.
    *
    * This must be a string where packages are delimited by '/' and classes by '.', e.g. "kotlin/Map.Entry".
    */
   const val TestEngineClassName = "io/kotest/engine/TestEngineLauncher"

   // the method invoked to add specs to the launcher, must exist on TestEngineLauncher
   const val WithSpecsMethodName = "withSpecs"

   // the method invoked to add configs on the launcher, must exist on TestEngineLauncher
   const val WithConfigMethodName = "withProjectConfig"

   // the method invoked to set the team city listener, must exist on TestEngineLauncher
   const val WithTeamCityListenerMethodName = "withTeamCityListener"

   // the method invoked to set the invoked platform, must exist on TestEngineLauncher
   const val WithJsMethodName = "withJs"

   // the method invoked to set the invoked platform, must exist on TestEngineLauncher
   const val WithNativeMethodName = "withNative"
}
