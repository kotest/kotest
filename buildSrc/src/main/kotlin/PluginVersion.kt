package plugin

data class PluginDescriptor(
   val since: String, // earliest version string this is compatible with
   val until: String, // latest version string this is compatible with, can be wildcard like 202.*
   val sdkVersion: String, // the version string passed to the intellij sdk gradle plugin
   val productName: String, // used as the source root for specifics of this build
   val deps: List<String> // dependent plugins of this plugin
)
