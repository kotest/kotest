package io.kotest.assertions.plugin.compiler

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.toLogger
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.builders.constFalse
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrConstantPrimitiveImpl
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.jvm.isJvm

@OptIn(ExperimentalCompilerApi::class)
class KotestAssertionsComponentRegistrar : CompilerPluginRegistrar() {
   override val supportsK2 = true
   override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
      println("QWEQEWWQE")
      val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
      messageCollector.toLogger().warning("Installing Kotest AssertionsIrGenerationExtension")
      IrGenerationExtension.registerExtension(AssertionsIrGenerationExtension(messageCollector))
   }
}

class AssertionsIrGenerationExtension(private val messageCollector: MessageCollector) : IrGenerationExtension {

   override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
      val platform = pluginContext.platform

      val transformer = when {
         platform.isJvm() -> JvmTransformer(messageCollector, pluginContext)
         else -> throw UnsupportedOperationException("Cannot use Kotest compiler plugin with platform: $platform")
      }

      moduleFragment.transform(transformer, null)
   }
}

class JvmTransformer(
   private val messageCollector: MessageCollector,
   private val pluginContext: IrPluginContext
) : IrElementTransformerVoidWithContext() {

   override fun visitCall(expression: IrCall): IrExpression {
      super.visitCall(expression)
      return when (expression.symbol.owner.kotlinFqName.asString()) {

         "io.kotest.matchers.shouldBe" -> if (expression.symbol.owner.isInfix) {
            val fn = pluginContext.referenceFunctions(
               CallableId(
                  FqName("io.kotest.matchers"),
                  Name.identifier("enhancedShouldBe")
               )
            ).single()

            val receiverAndArgs = expression.receiverAndArgs()
            val actual = receiverAndArgs.first()
            val expected = receiverAndArgs[1]

            // we only care if the actual is a method invocation, if it's a constant, then we can just
            // use the normal shouldBe with the value

            if (actual is IrCallImpl) {

               val actualRef = when (val prop = actual.symbol.owner.correspondingPropertySymbol) {
                  null -> null
                  else -> "property '${prop.owner.name.asString()}'"
               }

               return if (actualRef == null) expression else IrCallImpl(
                  startOffset = 0,
                  endOffset = 0,
                  type = fn.owner.returnType,
                  symbol = fn,
                  typeArgumentsCount = 1,
                  valueArgumentsCount = 3,
                  origin = expression.origin,
                  superQualifierSymbol = expression.superQualifierSymbol,
               ).also {
                  it.putTypeArgument(0, pluginContext.irBuiltIns.anyType)
                  it.putValueArgument(0, actual)
                  it.putValueArgument(1, expected)
                  it.putValueArgument(2, IrConstImpl.string(0, 0, pluginContext.irBuiltIns.stringType, actualRef))
               }
            } else {
               expression
            }
         } else expression

         else -> expression
      }
   }
}

object Constants {
   // the FQN for the enhanced shouldBe function
   const val TestEngineClassName = "io.kotest.matchers.wibbleShould"
}
