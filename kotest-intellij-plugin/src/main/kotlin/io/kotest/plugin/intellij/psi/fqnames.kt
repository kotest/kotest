package io.kotest.plugin.intellij.psi

import org.jetbrains.kotlin.idea.base.psi.kotlinFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject

fun KtClassOrObject.fqname(): FqName? = this.kotlinFqName
