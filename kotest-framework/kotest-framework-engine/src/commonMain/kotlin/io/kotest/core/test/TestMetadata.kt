package io.kotest.core.test

import kotlin.reflect.KClass

/**
 * A type-safe key for storing arbitrary metadata on tests and specs.
 *
 * Equality is based on [name] and [type], so two keys with the same name but
 * different types are treated as distinct. This prevents runtime
 * ClassCastException when two unrelated modules declare a key with the same
 * string name but different value types.
 *
 * Usage:
 * ```
 * val Issue = MetadataKey<String>("Issue")
 *
 * class MyTest : FunSpec({
 *    metadata[Issue] = "https://github.com/..."
 * })
 * ```
 */
class MetadataKey<T : Any>(val name: String, val type: KClass<T>) {

   override fun equals(other: Any?): Boolean =
      other is MetadataKey<*> && name == other.name && type == other.type

   override fun hashCode(): Int = 31 * name.hashCode() + type.hashCode()

   override fun toString(): String = "MetadataKey($name: ${type.simpleName})"

   companion object {
      inline operator fun <reified T : Any> invoke(name: String): MetadataKey<T> =
         MetadataKey(name, T::class)
   }
}

/**
 * A mutable, type-safe container for test metadata, used during registration
 * (spec init blocks, test.config() calls).
 *
 * At resolve time the engine converts this to a [ResolvedTestMetadata] —
 * an immutable snapshot — before attaching it to a [TestCase].
 */
class TestMetadata(
   private val entries: MutableMap<MetadataKey<*>, Any> = mutableMapOf()
) {

   operator fun <T : Any> get(key: MetadataKey<T>): T? {
      @Suppress("UNCHECKED_CAST")
      return entries[key] as? T
   }

   operator fun <T : Any> set(key: MetadataKey<T>, value: T) {
      entries[key] = value
   }

   fun isEmpty(): Boolean = entries.isEmpty()

   fun isNotEmpty(): Boolean = entries.isNotEmpty()

   fun keys(): Set<MetadataKey<*>> = entries.keys.toSet()

   /**
    * Returns an immutable [ResolvedTestMetadata] snapshot.
    */
   fun snapshot(): ResolvedTestMetadata = ResolvedTestMetadata(entries.toMap())

   /**
    * Returns a new [TestMetadata] that merges this metadata with [parent].
    * This metadata's entries take precedence over the parent's (child wins).
    */
   fun mergeWith(parent: TestMetadata): TestMetadata {
      val merged = mutableMapOf<MetadataKey<*>, Any>()
      merged.putAll(parent.entries)
      merged.putAll(this.entries) // child wins
      return TestMetadata(merged)
   }

   /**
    * Returns a read-only copy of all entries.
    */
   fun toMap(): Map<MetadataKey<*>, Any> = entries.toMap()

   internal val entriesForMerge: Map<MetadataKey<*>, Any> get() = entries
}

/**
 * Immutable resolved metadata attached to a [TestCase]. Safe to read
 * concurrently from extensions and listeners.
 */
class ResolvedTestMetadata internal constructor(
   private val entries: Map<MetadataKey<*>, Any>
) {

   operator fun <T : Any> get(key: MetadataKey<T>): T? {
      @Suppress("UNCHECKED_CAST")
      return entries[key] as? T
   }

   fun isEmpty(): Boolean = entries.isEmpty()

   fun isNotEmpty(): Boolean = entries.isNotEmpty()

   fun keys(): Set<MetadataKey<*>> = entries.keys

   fun toMap(): Map<MetadataKey<*>, Any> = entries

   companion object {
      val EMPTY = ResolvedTestMetadata(emptyMap())
   }
}
