package plugin

data class PluginDescriptor(
   val since: String,
   val until: String,
   val sdk: String,
   val deps: List<String>
)
