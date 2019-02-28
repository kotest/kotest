package io.kotlintest.plugin.intellij.intentions

import org.jetbrains.kotlin.name.FqName

class SoftAssertIntention : SurroundSelectionWithFunctionIntention() {

  override val function: String = "assertSoftly"

  override val importFQN: FqName = FqName("io.kotlintest.$function")

  override fun getText(): String = "Surround statements with soft assert"

  override fun getFamilyName(): String = text

}