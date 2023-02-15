import Kotest_publishing_test_gradle.KotestPublishingTestExtension.Companion.KOTEST_PUBLICATION_TEST_ATTRIBUTE
import Kotest_publishing_test_gradle.KotestPublishingTestExtension.PublicationTarget

/**
 * ### [`kotest-publishing-test.gradle.kts`][Kotest_publishing_test_gradle]
 *
 * Utility for running local Integration Tests that use Kotest artifacts published into Maven repositories.
 *
 * Using Maven Publications can give a more true-to-life experience, as test-projects must resolve
 * Kotest dependencies using a Maven repository.
 *
 * For example usage see subprojects that apply the `kotest-publishing-test` plugin.
 *
 * The [`kotest-publishing-conventions.gradle.kts`][Kotest_publishing_conventions_gradle] convention plugin
 * automatically applies the [`kotest-publishing-test.gradle.kts`][Kotest_publishing_test_gradle] plugin.
 *
 * @param testMavenRepoDir The project-local directory into which subprojects will publish their Maven artifacts
 */
abstract class KotestPublishingTestExtension(
   val testMavenRepoDir: Provider<Directory>
) {
   companion object {
      /** Unique Kotest [Attribute] used to discriminate between Gradle Configurations */
      val KOTEST_PUBLICATION_TEST_ATTRIBUTE = Attribute.of("io.kotest.publishing-test", String::class.java)
   }

   enum class PublicationTarget(
      /** Prefixes of all Gradle publishing tasks that will publish this target */
      val taskNamePrefixes: List<String>,
   ) {
      JS(
         "publishKotlinMultiplatform",
         "publishJs",
      ),
      JVM(
         "publishKotlinMultiplatform",
         "publishJvm",
      ),
      NATIVE(
         "publishKotlinMultiplatform",
         "publishIosArm32",
         "publishIosArm64",
         "publishIosSimulatorArm64",
         "publishIosX64",
         "publishLinuxX64",
         "publishMacosArm64",
         "publishMacosX64",
         "publishMingwX64",
         "publishTvosArm64",
         "publishTvosSimulatorArm64",
         "publishTvosX64",
         "publishWatchosArm32",
         "publishWatchosArm64",
         "publishWatchosSimulatorArm64",
         "publishWatchosX64",
         "publishWatchosX86",
      ),
      ;

      val prettyName: String = name.toLowerCase().capitalize()

      constructor(vararg taskNamePrefixes: String) : this(taskNamePrefixes.asList())

      companion object {
         /** [Attribute] used to discriminate between different Kotlin targets */
         val attribute = Attribute.of("io.kotest.publishing-test.target", PublicationTarget::class.java)

      }
   }
}

val publishingTestExtension = extensions.create(
   "kotestPublishingTest",
   KotestPublishingTestExtension::class,
   rootProject.layout.buildDirectory.dir("test-maven-repo"),
)


dependencies.attributesSchema {
   // register the custom Attributes, so Gradle is aware of them
   attribute(KOTEST_PUBLICATION_TEST_ATTRIBUTE)
   attribute(PublicationTarget.attribute)
}

/**
 * Use this [Configuration] to depend on Maven Publications from other subprojects.
 */
val testMavenPublication by configurations.registering {
   description = "Depend on the Maven Publication of other subprojects (for integration tests)"
   isCanBeConsumed = false
   isCanBeResolved = true
   isVisible = false
   attributes {
      attribute(KOTEST_PUBLICATION_TEST_ATTRIBUTE, "maven-publication")
   }
}

// register target-specific consumers
PublicationTarget.values().forEach { target ->
   configurations.register("testMavenPublication${target.prettyName}") {
      description = "Depend on ${target.prettyName} Maven Publication of other subprojects (for integration tests)"
      isCanBeConsumed = false
      isCanBeResolved = true
      isVisible = false
      extendsFrom(testMavenPublication.get())
      attributes {
         attribute(KOTEST_PUBLICATION_TEST_ATTRIBUTE, "maven-publication")
         attribute(PublicationTarget.attribute, target)
      }
   }
}


// if this convention plugin is applied to a subproject that has a Maven Publication, then
// 1. add the test publication directory as a Maven Repo
// 2. register an *outgoing* Configuration, with a variant for each PublicationTarget
plugins.withType<PublishingPlugin>().configureEach {
   extensions.getByType<PublishingExtension>().apply {
      val kotestPublishingTestRepo = repositories.maven(publishingTestExtension.testMavenRepoDir) {
         name = "KotestPublishingTest"
      }

      /** The [Configuration] that provides Maven Publications to other subprojects. */
      configurations.register("testMavenPublicationProvider") mainProvider@{
         description = "Provides Maven Publications to other subprojects"
         isCanBeConsumed = true
         isCanBeResolved = false
         isVisible = true
         attributes {
            attribute(KOTEST_PUBLICATION_TEST_ATTRIBUTE, "maven-publication")
         }

         // propagate incoming Maven Publications to dependent subprojects
         extendsFrom(testMavenPublication.get())

         outgoing {

            // register a variant for each target
            PublicationTarget.values().forEach { target ->
               variants.create(this@mainProvider.name + target.prettyName) {
                  attributes {
                     attribute(KOTEST_PUBLICATION_TEST_ATTRIBUTE, "maven-publication")
                     attribute(PublicationTarget.attribute, target)
                  }

                  artifact(publishingTestExtension.testMavenRepoDir) {
                     builtBy(
                        tasks.withType<PublishToMavenRepository>().matching { task ->
                           task.repository == kotestPublishingTestRepo
                              && target.taskNamePrefixes.any { task.name.startsWith(it) }
                        }
                     )
                  }
               }
            }
         }
      }
   }
}
