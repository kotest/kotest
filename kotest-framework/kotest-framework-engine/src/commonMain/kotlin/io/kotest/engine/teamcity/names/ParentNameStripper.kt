package io.kotest.engine.teamcity.names

import io.kotest.core.names.TestName

// Note: intellij has a bug, where if a child test has a name that starts with the parent test name,
// then it will remove the common prefix from the child, to workaround this, we will add a dash at the
// start of the nested test to make the child nest have a different prefix.
// Also note: This only affects non-MPP tests, as MPP tests have the platform name added
internal object ParentNameStripper {
   fun stripe(name: TestName, parent: TestName?): String {
      return when {
         parent == null -> name.name
         name.name.startsWith(parent.name) -> "- " + name.name
         else -> name.name
      }
   }
}
