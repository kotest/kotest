package io.kotlintest.plugin.intellij.intentions

import org.jetbrains.kotlin.name.FqName

class ShouldThrowIntention : SurroundSelectionWithBlockIntention() {

  override fun getText(): String = "Surround statements with shouldThrow assertion"

  override fun getFamilyName(): String = text

  override val prefix: String = "shouldThrow<Exception>"

  override val importFQN: FqName = FqName("io.kotlintest.shouldThrow")
}