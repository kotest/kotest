package io.kotest.plugin.intellij

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.ConfigurationFromContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.buildSuggestedName
import io.kotest.plugin.intellij.psi.enclosingClass
import io.kotest.plugin.intellij.styles.BehaviorSpecStyle
import io.kotest.plugin.intellij.styles.DescribeSpecStyle
import io.kotest.plugin.intellij.styles.ExpectSpecStyle
import io.kotest.plugin.intellij.styles.FeatureSpecStyle
import io.kotest.plugin.intellij.styles.FreeSpecStyle
import io.kotest.plugin.intellij.styles.FunSpecStyle
import io.kotest.plugin.intellij.styles.ShouldSpecStyle
import io.kotest.plugin.intellij.styles.SpecStyle
import io.kotest.plugin.intellij.styles.StringSpecStyle
import io.kotest.plugin.intellij.styles.WordSpecStyle
import removeJUnitRunConfigs

/**
 * A run configuration contains the details of a particular run (in the drop down run box).
 * A Run producer is called to configure a [KotestRunConfiguration] after it has been created.
 */
abstract class TestPathRunConfigurationProducer(private val style: SpecStyle) : LazyRunConfigurationProducer<KotestRunConfiguration>() {

   /**
    * Returns the [KotestConfigurationFactory] used to create [KotestRunConfiguration]s.
    */
   override fun getConfigurationFactory(): ConfigurationFactory = KotestConfigurationFactory(KotestConfigurationType)

   /**
    * Returns true if the given context is applicable to this run producer.
    * This implementation will return true if the source element is a test in the producers defined [style].
    */
   override fun setupConfigurationFromContext(configuration: KotestRunConfiguration,
                                              context: ConfigurationContext,
                                              sourceElement: Ref<PsiElement>): Boolean {
      val element = sourceElement.get()
      if (element != null) {
         val test = style.test(element)
         if (test != null) {

            val ktclass = element.enclosingClass()
            if (ktclass != null) {

               configuration.setTestName(test.path)
               configuration.setSpec(ktclass)
               configuration.setModule(context.module)
               configuration.setGeneratedName()

               //context.project.getComponent(ElementLocationCache::class.java).add(ktclass)
               removeJUnitRunConfigs(context.project, ktclass.fqName!!.shortName().asString())
               return true
            }
         }
      }

      return false
   }

   // compares the existing configurations to the context in question
   // if one of the configurations matches then this should return true
   override fun isConfigurationFromContext(configuration: KotestRunConfiguration,
                                           context: ConfigurationContext): Boolean {
      val element = context.psiLocation
      if (element != null) {
         val test = style.test(element)
         if (test != null) {
            val spec = element.enclosingClass()
            val name = buildSuggestedName(spec?.fqName?.asString(), test.path)
            return configuration.name == name
         }
      }
      return false
   }

   /**
    * When two configurations are created from the same context by two different producers, checks if the configuration created by
    * this producer should be discarded in favor of the other one.
    *
    * We always return true because no one else should be creating Kotest configurations.
    */
   override fun isPreferredConfiguration(self: ConfigurationFromContext?, other: ConfigurationFromContext?): Boolean {
      println("isPreferredConfiguration self=$self other=$other")
      return true
   }

   override fun shouldReplace(self: ConfigurationFromContext, other: ConfigurationFromContext): Boolean {
      println("shouldReplace self=$self other=$other")
      return false
   }
}

class FunSpecRunConfigurationProducer : TestPathRunConfigurationProducer(FunSpecStyle)
class BehaviorSpecRunConfigurationProducer : TestPathRunConfigurationProducer(BehaviorSpecStyle)
class ShouldSpecRunConfigurationProducer : TestPathRunConfigurationProducer(ShouldSpecStyle)
class StringSpecRunConfigurationProducer : TestPathRunConfigurationProducer(StringSpecStyle)
class WordSpecRunConfigurationProducer : TestPathRunConfigurationProducer(WordSpecStyle)
class FeatureSpecRunConfigurationProducer : TestPathRunConfigurationProducer(FeatureSpecStyle)
class ExpectSpecRunConfigurationProducer : TestPathRunConfigurationProducer(ExpectSpecStyle)
class FreeSpecRunConfigurationProducer : TestPathRunConfigurationProducer(FreeSpecStyle)
class DescribeSpecRunConfigurationProducer : TestPathRunConfigurationProducer(DescribeSpecStyle)
