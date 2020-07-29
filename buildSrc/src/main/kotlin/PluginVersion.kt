package plugin

data class PluginDescriptor(
   val since: String,
   val until: String,
   val version: String,
   val deps: List<String>
)
