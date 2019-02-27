package io.kotlintest.plugin.intellij.intentions

import org.jetbrains.kotlin.name.FqName

class SoftAssertIntention : SurroundSelectionWithBlockIntention() {

  override val prefix: String = "assertSoftly"

  override val importFQN: FqName = FqName("io.kotlintest.$prefix")

  override fun getText(): String = "Surround statements with soft assert"

  override fun getFamilyName(): String = text

}