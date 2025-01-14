package io.kotest.framework.launcher

/**
 * Parses args in the format --name value.
 */
internal fun parseArgs(args: List<String>): Map<String, String> {
   val argsmap = mutableMapOf<String, String>()
   var name = ""
   var value = ""
   args.forEach {
      if (it.startsWith("--")) {
         if (name.isNotBlank()) {
            argsmap[name] = value
            value = ""
         }
         name = it.drop(2)
      } else {
         value = if (value.isEmpty()) it else "$value $it"
      }
   }
   if (name.isNotBlank())
      argsmap[name] = value
   return argsmap.toMap()
}
