package io.kotest.engine.config

import io.kotest.core.config.configuration
import io.kotest.core.extensions.RuntimeTagExtension
import io.kotest.engine.extensions.RuntimeTagExpressionExtension
import io.kotest.engine.extensions.SystemPropertyTagExtension
import java.util.concurrent.atomic.AtomicBoolean

object ConfigManager {

   // we only detect configuration once per jvm
   private val initialized = AtomicBoolean(false)

   /**
    *
    * This function will detect config from source such as classpath and system properties and apply
    * it to the configuration singleton. It should be invoked before we try to read anything from config.
    *
    * The function is idempotent.
    */
   fun init() {

      // detects project config  etc and then applies that to our configuration singleton
      if (initialized.compareAndSet(false, true)) {
         detectConfig().apply(configuration)

         // explicitly register default extensions
         configuration.registerExtensions(
            SystemPropertyTagExtension,
            RuntimeTagExtension,
            RuntimeTagExpressionExtension
         )
      }
   }
}
