package io.kotest.plugin.intellij.intentions

import org.jetbrains.kotlin.name.FqName

class ShouldThrowIntention : SurroundSelectionWithFunctionIntention() {

  override fun getText(): String = "Surround statements with shouldThrow assertion"

  override fun getFamilyName(): String = text

  override val function: String = "shouldThrow<Exception>"

  override val importFQN: FqName = FqName("io.kotest.assertions.throwables.shouldThrow")
}