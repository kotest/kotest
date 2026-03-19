package io.kotest.plugin.intellij.intentions

import org.jetbrains.kotlin.name.FqName

class ShouldThrowAnyIntention : SurroundSelectionWithFunctionIntention() {

  override fun getText(): String = "Surround statements with shouldThrowAny assertion"

  override fun getFamilyName(): String = text

  override val function: String = "shouldThrowAny"

  override val importFQN: FqName = FqName("io.kotest.assertions.throwables.shouldThrowAny")
}