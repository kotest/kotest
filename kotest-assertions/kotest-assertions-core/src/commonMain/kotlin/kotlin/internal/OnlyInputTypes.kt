package kotlin.internal

/**
 * Shadows [kotlin.internal.OnlyInputTypes] from the Kotlin standard library.
 *
 * When applied to a type parameter, the compiler restricts type inference so that `T`
 * must be determined solely from input positions (receiver, arguments, expected type).
 * This prevents the compiler from widening `T` to a common supertype such as `Any`.
 *
 * If a future Kotlin version stops recognising the shadowed annotation,
 * functions annotated with it will simply lose the type restriction and behave
 * like ordinary generic functions — no compilation errors will occur.
 */
@Target(AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.BINARY)
internal annotation class OnlyInputTypes
