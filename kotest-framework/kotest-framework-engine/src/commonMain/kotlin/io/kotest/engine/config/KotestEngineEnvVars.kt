package io.kotest.engine.config

object KotestEngineEnvVars {

   /**
    * Sets the tag expression that determines included/excluded tags.
    */
   internal const val TAG_EXPRESSION = "KOTEST_TAGS"

   /**
    * Sets the invocation count that has been sent by the IJ plugin,
    * allowing to run tests N times
    */
   internal const val INVOCATION_COUNT = "KOTEST_INVOCATION_COUNT"
}
