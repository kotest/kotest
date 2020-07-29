package plugin

data class PluginDescriptor(
   val since: String,
   val until: String,
   val sdkVersion: String,
   val productName: String,
   val deps: List<String>
)
