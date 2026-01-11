package io.kotest.common

private const val IDEA_ACTIVE_SYSPROP = "idea.active"
private const val IDEA_ACTIVE_ENV = "IDEA_ACTIVE"

/**
 * Returns true if this is running inside intellij.
 *
 * The [IDEA_ACTIVE_SYSPROP] is set by intellij and the [IDEA_ACTIVE_ENV] is set by us from the Kotest gradle plugin.
 */
fun isIntellij(): Boolean {
   return sysprop(IDEA_ACTIVE_SYSPROP) != null || env(IDEA_ACTIVE_ENV) != null
}
