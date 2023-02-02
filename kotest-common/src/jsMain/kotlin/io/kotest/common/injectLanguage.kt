@file:Suppress("PackageDirectoryMismatch")
package org.intellij.lang.annotations

import io.kotest.common.KotestInternal

@KotestInternal
@Retention(AnnotationRetention.BINARY)
@Target(
   AnnotationTarget.FUNCTION,
   AnnotationTarget.PROPERTY_GETTER,
   AnnotationTarget.PROPERTY_SETTER,
   AnnotationTarget.FIELD,
   AnnotationTarget.VALUE_PARAMETER,
   AnnotationTarget.LOCAL_VARIABLE,
   AnnotationTarget.ANNOTATION_CLASS,
)
actual annotation class Language actual constructor(
   actual val value: String,
   actual val prefix: String,
   actual val suffix: String
)
