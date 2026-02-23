package io.kotest.engine.names

import io.kotest.common.KotestInternal
import io.kotest.core.descriptors.Descriptor

@KotestInternal
object LocationEmbedder {

   const val OPEN_TAG = "<kotest>"
   const val CLOSE_TAG = "</kotest>"

   /**
    * Since we have no control over the proxy location urls created by intellij, we will include the full
    * test path in the display name and use the kotest intellij plugin to parse it out.
    *
    * Note: the KMP test tasks will append a context e.g. `linuxX64`, so we must put the full path first.
    *
    * Note2: When constructing its SMTProxy tree, for top level nodes, intellij expects a FQN, so, for
    * specs we just display the FQN and don't mangle them.
    *
    * Note3: For TCSM generated trees, Intellij will parse out anything before the last period as the package name.
    * So given a path like <kotest>com.sksamuel.Spec</kotest>Spec, Intellij will interpret <kotest>com.sksamuel as
    * the package name. Therefore, we must replace periods.
    */
   fun embeddedTestName(descriptor: Descriptor, formattedName: String): String {
// todo native support     return OPEN_TAG + descriptor.path().value.replace(' ', '\u00A0').replace('.', '!') + CLOSE_TAG + formattedName
      return OPEN_TAG + descriptor.path().value + CLOSE_TAG + formattedName
   }
}
