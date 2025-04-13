package io.kotest.framework.multiplatform.embeddablecompiler

internal object EntryPoint {

   // we use a public val to register each spec
   const val LauncherValName = "launcher"

   // the method invoked to start the tests, must exist on TestEngineLauncher
   const val LaunchMethodName = "launch"

   // the method invoked on TestEngineLauncher to start the tests
   // in JS we use promise() which ultimately calls into GlobalScope.promise on JS platforms
   const val PROMISE_FUNCTION_NAME = "promise"

   // this is the name of the function that is generated that contains the setup and launch of the test engine
   // eg, this function would look something like `fun runKotest() { TestEngineLauncher().withSpecs(...).launch() }
   const val RUN_TESTS_FUNCTION_NAME = "runKotest"

   const val JS_EXPORT_ANNOTATION_CLASS_NAME = "kotlin/js/JsExport"

   /**
    * the FQN for the class used to launch the MPP engine.
    *
    * This must be a string where packages are delimited by '/' and classes by '.', e.g. "kotlin/Map.Entry".
    */
   const val TEST_ENGINE_CLASS_NAME = "io/kotest/engine/TestEngineLauncher"

   // the method invoked to add specs to the launcher, must exist on TestEngineLauncher
   const val WITH_SPECS_FUNCTION_NAME = "withSpecs"

   // the method invoked to set config on the launcher, must exist on TestEngineLauncher
   const val WITH_PROJECT_CONFIG_METHOD_NAME = "withProjectConfig"

   // the method invoked to set the team city listener, must exist on TestEngineLauncher
   const val WithTeamCityListenerMethodName = "withTeamCityListener"

   // the method invoked to set the invoked platform, must exist on TestEngineLauncher
   const val WITH_JS_METHOD_NAME = "withJs"

   // the method invoked to set the invoked platform, must exist on TestEngineLauncher
   const val WITH_WASM_JS_METHOD_NAME = "withWasmJs"

   // the method invoked to set the invoked platform, must exist on TestEngineLauncher
   const val WithNativeMethodName = "withNative"
}
