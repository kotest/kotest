package io.kotest.engine.names

import io.kotest.common.KotestInternal
import io.kotest.core.descriptors.Descriptor

@KotestInternal
object LocationEmbedder {

   const val OPEN_TAG = "<kotest>"
   const val CLOSE_TAG = "</kotest>"

   /**
    * Since we have no control over the proxy location urls created by intellij, we will include the full
    * test path in the display name and use the kotest intellij plugin to parse it out
    * note: the KMP test tasks will append a context e.g. `linuxX64`, so we must put the full path first.
    */
   fun embeddedTestName(descriptor: Descriptor, formattedName: String): String {
      return OPEN_TAG + descriptor.path().value + CLOSE_TAG + formattedName
   }
}
