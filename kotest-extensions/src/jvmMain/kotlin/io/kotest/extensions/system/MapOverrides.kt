package io.kotest.extensions.system

sealed class OverrideMode {

   abstract fun override(originalValues: Map<String, String>,
                         newValues: Map<String, String?>): MutableMap<String, String>

   /**
    * Sets specific values and overrides pre-existent ones, if any
    *
    * Any values that are not present in the overrides will be left untouched.
    */
   object SetOrOverride : OverrideMode() {
      override fun override(originalValues: Map<String, String>, newValues: Map<String, String?>) =
         originalValues.toMutableMap().apply { putReplacingNulls(newValues) }
   }

   /**
    * Sets specific values, ignoring pre-existent ones, if any
    *
    * Any values that are not present in the overrides will be left untouched.
    */
   object SetOrIgnore : OverrideMode() {
      override fun override(originalValues: Map<String, String>, newValues: Map<String, String?>) =
         originalValues.toMutableMap().apply { putWithoutReplacements(newValues) }

      private fun MutableMap<String, String>.putWithoutReplacements(map: Map<String, String?>) {
         map.forEach { (key, value) ->
            value?.let { this.putIfAbsent(key, it) }
         }
      }
   }

   /**
    * Sets specific values and throws an exception if the chosen key already exists
    *
    * Any values that are not present in the overrides will be left untouched.
    */
   object SetOrError : OverrideMode() {
      override fun override(originalValues: Map<String, String>,
                            newValues: Map<String, String?>): MutableMap<String, String> {
         return if (newValues.keys.any { it in originalValues.keys }) {
            throw IllegalOverrideException(newValues)
         } else {
            SetOrOverride.override(originalValues, newValues)
         }
      }

      class IllegalOverrideException(values: Map<String, String?>) : IllegalArgumentException("Overriding a variable when mode is set to SetOrError. Use another OverrideMode to allow this. Trying to set $values")
   }
}

@PublishedApi
internal fun MutableMap<String, String>.putReplacingNulls(map: Map<String, String?>) {
   map.forEach { (key, value) ->
      if (value == null) remove(key) else put(key, value)
   }
}
