package io.kotest.framework.multiplatform.native

object EntryPoint {
   // we use a public val to register each spec
   const val LauncherValName = "launcher"

   // the method invoked to start the tests, must exist on TestEngineLauncher
   const val LaunchMethodName = "launch"

   // the FQN for the class used to launch the MPP engine
   const val TestEngineClassName = "io.kotest.engine.TestEngineLauncher"

   // the method invoked to add specs to the launcher, must exist on TestEngineLauncher
   const val WithSpecsMethodName = "withSpecs"

   // the method invoked to set the team city listener, must exist on TestEngineLauncher
   const val WithTeamCityListenerMethodName = "withTeamCityListener"
}
