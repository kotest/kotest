package io.kotlintest.plugin.intellij.intentions

import org.jetbrains.kotlin.name.FqName

class ShouldThrowAnyIntention : SurroundSelectionWithBlockIntention() {

  override fun getText(): String = "Surround statements with shouldThrowAny assertion"

  override fun getFamilyName(): String = text

  override val prefix: String = "shouldThrowAny"

  override val importFQN: FqName = FqName("io.kotlintest.shouldThrowAny")
}