package io.kotlintest

/**
 * A name slash id for this spec which is used as the parent route for tests.
 * By default this will return the fully qualified class name, unless the spec
 * class is annotated with @DisplayNamen.
 *
 * Note: This name must be globally unique. Two specs, even in different packages,
 * cannot share the same name.
 */
fun Class<out Spec>.displayName(): String {
  val displayName = annotations.find { it is DisplayName }
  return when (displayName) {
    is DisplayName -> displayName.name
    else -> canonicalName
  }
}

fun Class<out Spec>.description() = Description.spec(this.displayName())