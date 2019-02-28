package io.kotlintest.plugin.intellij.intentions

import org.jetbrains.kotlin.name.FqName

class ShouldThrowExactlyIntention : SurroundSelectionWithFunctionIntention() {

  override fun getText(): String = "Surround statements with shouldThrowExactly assertion"

  override fun getFamilyName(): String = text

  override val function: String = "shouldThrowExactly<Exception>"

  override val importFQN: FqName = FqName("io.kotlintest.shouldThrowExactly")
}