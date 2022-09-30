package io.kotest.framework.multiplatform.embeddablecompiler

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationBase
import org.jetbrains.kotlin.ir.declarations.IrDeclarationContainer
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import java.io.File

// These extension properties are available in org.jetbrains.kotlin.ir.declarations, but were moved from one file to
// another in Kotlin 1.7. This breaks backwards compatibility with earlier versions of Kotlin.
// So instead of using the provided implementations, we've copied them here, so we can work with both Kotlin 1.7+ and earlier
// versions without issue.
// See https://github.com/kotest/kotest/issues/3060 and https://youtrack.jetbrains.com/issue/KT-52888 for more information.
internal val IrFile.path: String get() = fileEntry.name
internal val IrFile.name: String get() = File(path).name

// These extension methods were moved from org.jetbrains.kotlin.backend.common.ir to org.jetbrains.kotlin.ir.util in Kotlin 1.7.20.
// (see https://github.com/JetBrains/kotlin/commit/f3252334b2ef679aa40e94002d9e65b1b76e95b6)
// For similar reasons to above, we've copied them here so the compiler plugin can work with Kotlin 1.6, 1.7.0 and 1.7.20.
fun IrDeclarationContainer.addChild(declaration: IrDeclaration) {
   this.declarations += declaration
   declaration.setDeclarationsParent(this)
}

fun <T : IrElement> T.setDeclarationsParent(parent: IrDeclarationParent): T {
   accept(SetDeclarationsParentVisitor, parent)
   return this
}

object SetDeclarationsParentVisitor : IrElementVisitor<Unit, IrDeclarationParent> {
   override fun visitElement(element: IrElement, data: IrDeclarationParent) {
      if (element !is IrDeclarationParent) {
         element.acceptChildren(this, data)
      }
   }

   override fun visitDeclaration(declaration: IrDeclarationBase, data: IrDeclarationParent) {
      declaration.parent = data
      super.visitDeclaration(declaration, data)
   }
}
