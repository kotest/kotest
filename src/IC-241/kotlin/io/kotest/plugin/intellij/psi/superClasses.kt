package io.kotest.plugin.intellij.psi

import org.jetbrains.kotlin.idea.caches.resolve.analyze
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.classId
import org.jetbrains.kotlin.types.typeUtil.supertypes

/**
 * Recursively returns the list of classes and interfaces extended or implemented by the class.
 */
fun KtClassOrObject.getAllSuperClasses(): List<FqName> {
   return superTypeListEntries
      .mapNotNull { it.typeReference }
      .mapNotNull {
         runCatching {
            val bindingContext = it.analyze()
            bindingContext.get(BindingContext.TYPE, it)
         }.getOrNull()
      }.flatMap {
         runCatching {
            it.supertypes() + it
         }.getOrElse { emptyList() }
      }.mapNotNull {
         runCatching {
            it.constructor.declarationDescriptor.classId
         }.getOrNull()
      }.mapNotNull {
         runCatching {
            val packageName = it.packageFqName
            val simpleName = it.relativeClassName
             FqName("$packageName.$simpleName")
         }.getOrNull()
      }.filterNot { it.toString() == "kotlin.Any" }
}
