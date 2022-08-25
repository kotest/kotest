package io.kotest.plugin.intellij.toolwindow

object TestExplorerState {

   var showCallbacks = true
   var showTags = true
   var showModules = true
   var showIncludes = true
   var autoscrollToSource = true

   var tags: List<String> = emptyList()
}
