package io.kotest.assertions.plugin.jvm

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.toLogger
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetValue

class ShouldBeGenerationExtension(private val messageCollector: MessageCollector) : IrGenerationExtension {

   override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

      moduleFragment.transform(object : IrElementTransformerVoidWithContext() {
         var configs = mutableListOf<IrClass>()

         override fun visitClassNew(declaration: IrClass): IrStatement {
            return super.visitClassNew(declaration)
         }

         override fun visitFileNew(declaration: IrFile): IrFile {
            return super.visitFileNew(declaration)
         }

         override fun visitCall(expression: IrCall): IrExpression {
            return if (expression.isShouldBe()) {
               // we know shouldBe is an extension method as it's a kotest function, and just in case
               // someone else created a shouldBe we check for the receiver
               val receiver = expression.extensionReceiver!!
               messageCollector.toLogger().warning(receiver::class.toString())
               when (receiver) {
                  is IrConst<*> -> handleConstant(receiver)
                  is IrCall -> handleCall(receiver)
                  else -> messageCollector.toLogger().warning("unhandled type !! $receiver")
               }
            } else super.visitCall(expression)
         }

         private fun handleConstant(receiver: IrConst<*>) {
            messageCollector.toLogger().warning("Constant: $receiver")
         }

         private fun handleGetValue(get: IrGetValue) {
            messageCollector.toLogger().warning("IrGetValue: ${get.symbol.owner.name}")
         }

         private fun handleCall(expression: IrCall): IrCall {
            messageCollector.toLogger().warning("Call: ${expression.symbol.owner.name}")
            return when (val receiver = expression.extensionReceiver) {
               is IrConst<*> -> handleConstant(receiver)
               is IrCall -> handleCall(receiver)
               null -> when (val dispatcher = expression.dispatchReceiver) {
                  is IrConst<*> -> handleConstant(dispatcher)
                  is IrCall -> handleCall(dispatcher)
                  is IrGetValue -> handleGetValue(dispatcher)
                  null -> return expression
                  else -> messageCollector.toLogger().warning("Unhandled dispatchReceiver: $dispatcher")
               }
               else -> messageCollector.toLogger().warning("Unhandled extensionReceiver: $receiver")
            }
         }

      }, null)
   }
}

fun IrCall.isShouldBe(): Boolean {
   return symbol.owner.name.asString() == "shouldBe"
      && symbol.owner.isInfix
      && valueArgumentsCount == 1
      && extensionReceiver != null
}
