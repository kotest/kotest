package io.kotest.plugin.intellij

import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject

fun KtClassOrObject.fqname(): FqName? = this.getKotlinFqName()
