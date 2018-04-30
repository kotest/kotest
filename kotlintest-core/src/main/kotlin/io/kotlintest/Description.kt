package io.kotlintest

/**
 * The description gives the full path to a [TestScope].
 *
 * It contains the name of every parent, with the root at index 0.
 * And it includes the name of the test scope it represents.
 *
 * This is useful when you want to write generic extensions and you
 * need to be able to filter on certain tests only.
 *
 * @param parents each parent scope
 * @param name the name of this scope
 */
data class Description(val parents: List<String>, val name: String) {

  companion object {
    fun root(name: String) = Description(emptyList(), name)
  }

  fun append(name: String) =
      Description(this.parents + this.name, name)

  fun hasParent(description: Description): Boolean = parents.containsAll(description.parents + listOf(description.name))

  fun parent(): Description? = if (isSpec()) null else Description(parents.dropLast(1), parents.last())

  fun isSpec(): Boolean = parents.isEmpty()

  fun isRoot(): Boolean = parents.isEmpty()

  fun head(): Description = Description(emptyList(), parents.first())

  fun tail() = if (parents.isEmpty()) throw NoSuchElementException() else Description(parents.drop(1), name)

  fun fullName(): String = (parents + listOf(name)).joinToString(" ")

  /**
   * Returns a String version of this description, which is
   * the parents + this name concatenated with slashes.
   */
  fun id(): String = (parents + listOf(name)).joinToString("/")

  fun names(): List<String> = parents + name

  fun depth() = names().size

  /**
   * Returns true if this instance is the immediate parent of the supplied argument.
   */
  fun isParentOf(description: Description): Boolean =
      parents + name == description.parents

  /**
   * Returns true if this instance is an ancestor of the supplied argument.
   */
  fun isAncestorOf(description: Description): Boolean {
    if (isParentOf(description))
      return true
    val p = description.parent() ?: return false
    return isAncestorOf(p)
  }
}