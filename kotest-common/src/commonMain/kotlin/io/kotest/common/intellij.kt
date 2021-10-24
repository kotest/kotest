package io.kotest.common

import io.kotest.mpp.sysprop

/**
 * Returns true if we are executing inside intellij.
 *
 * Note: This cannot be relied on for 100% accuracy.
 */
fun isIntellij(): Boolean {
   return sysprop("idea.test.cyclic.buffer.size") != null
      || (sysprop("jboss.modules.system.pkgs") ?: "").contains("com.intellij.rt")
      || sysprop("intellij.debug.agent") != null
      || (sysprop("java.class.path") ?: "").contains("idea_rt.jar")
}
