import Kotest_publishing_test_gradle.KotestPublishingTestExtension.Companion.KOTEST_PUBLICATION_TEST_ATTRIBUTE

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
      // Unique Kotest Attribute used to discriminate between Gradle Configurations
      val KOTEST_PUBLICATION_TEST_ATTRIBUTE = Attribute.of("io.kotest.publishing-test", String::class.java)
   }
}

val publishingTestExtension = extensions.create(
   "kotestPublishingTest",
   KotestPublishingTestExtension::class,
   rootProject.layout.buildDirectory.dir("test-maven-repo"),
)


// register the kotestPublishingTest custom Attribute, so Gradle is aware of it
dependencies.attributesSchema {
   attribute(KOTEST_PUBLICATION_TEST_ATTRIBUTE)
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

/**
 * The [Configuration] that provides Maven Publications to other subprojects.
 */
val testMavenPublicationProvider by configurations.registering {
   description = "Provides Maven Publications to other subprojects"
   isCanBeConsumed = true
   isCanBeResolved = false
   isVisible = true
   attributes {
      attribute(KOTEST_PUBLICATION_TEST_ATTRIBUTE, "maven-publication")
   }

   // propagate incoming Maven Publications to dependent subprojects
   extendsFrom(testMavenPublication.get())
}

// if this convention plugin is applied to a subproject that has a Maven Publication, then
plugins.withType<PublishingPlugin>().configureEach {
   // 1. add the test publication directory as a Maven Repo
   extensions.getByType<PublishingExtension>().apply {
      repositories {
         maven(publishingTestExtension.testMavenRepoDir) {
            name = "KotestPublishingTest"
         }
      }
   }

   // 2. add the Maven Repo as an outgoing file, which will trigger the publication task when
   //    the publication is requested by another subproject
   testMavenPublicationProvider.configure {
      outgoing {
         artifact(publishingTestExtension.testMavenRepoDir) {
            builtBy(
               tasks.withType<PublishToMavenRepository>()
                  .matching { it.repository.name == "KotestPublishingTest" }
            )
         }
      }
   }
}
