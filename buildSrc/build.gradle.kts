@file:Suppress("UnstableApiUsage")

import org.gradle.api.attributes.DocsType.DOCS_TYPE_ATTRIBUTE
import org.gradle.api.attributes.DocsType.SOURCES
import org.gradle.kotlin.dsl.support.expectedKotlinDslPluginsVersion
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
   `kotlin-dsl`
}

dependencies {
   implementation(libs.kotlin.gradle.plugin)
   implementation(libs.devPublish.plugin)
   implementation(libs.nmcp.plugin)
}

tasks.withType<AbstractArchiveTask>().configureEach {
   // https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives
   isPreserveFileTimestamps = false
   isReproducibleFileOrder = true
}

//region workaround for https://github.com/gradle/gradle/issues/13020
// Gradle _always_ logs an annoying warning: Unsupported Kotlin plugin version.
// The warning is always logged, no matter what, and it doesn't ever seem to represent an actual problem.
// So, this code downloads the original EmbeddedKotlinPlugin and disables the warning.
// Because Gradle's classloader is hierarchical, the original will be replaced.

val kotlinDslPluginSources: Configuration by configurations.creating {
   description = "Download the original Gradle Kotlin DSL plugin source code."
   isCanBeConsumed = false
   isCanBeResolved = false
   isCanBeDeclared = true
   isVisible = false
   defaultDependencies {
      add(project.dependencies.create("org.gradle.kotlin:gradle-kotlin-dsl-plugins:$expectedKotlinDslPluginsVersion"))
   }
}

val kotlinDslPluginSourcesResolver: Configuration by configurations.creating {
   description = "Resolve files from ${kotlinDslPluginSources.name}."
   isCanBeConsumed = false
   isCanBeResolved = true
   isCanBeDeclared = false
   isVisible = false
   extendsFrom(kotlinDslPluginSources)
   attributes {
      attribute(DOCS_TYPE_ATTRIBUTE, objects.named(SOURCES))
   }
}

val fixGradlePluginWarning by tasks.registering {
   description = "Download EmbeddedKotlinPlugin.kt and patch it to disable the warning."

   val src = kotlinDslPluginSourcesResolver.incoming.files
   inputs.files(src).withNormalizer(ClasspathNormalizer::class)

   outputs.dir(temporaryDir).withPropertyName("outputDir")

   val archives = serviceOf<ArchiveOperations>()

   doLast {
      val embeddedKotlinPlugin = src.flatMap { s ->
         archives.zipTree(s).matching {
            include("**/EmbeddedKotlinPlugin.kt")
         }
      }.firstOrNull()

      if (embeddedKotlinPlugin == null) {
         // If EmbeddedKotlinPlugin.kt can't be found then maybe this workaround
         // is no longer necessary, or it needs to be updated.
         logger.warn("[$path] could not find EmbeddedKotlinPlugin.kt in $src")
      } else {
         logger.info("[$path] Patching EmbeddedKotlinPlugin.kt to remove 'Unsupported Kotlin plugin version' warning")
         temporaryDir.deleteRecursively()
         temporaryDir.mkdirs()
         temporaryDir.resolve(embeddedKotlinPlugin.name).apply {
            writeText(
               embeddedKotlinPlugin.readText()
                  // This is the key change: converting 'warn' into 'info'.
                  .replace("\n        warn(\n", "\n        info(\n")
                  // Mark internal things as internal to prevent compiler warnings about unused code,
                  // and to stop them leaking into build scripts.
                  .replace("\n\nfun Logger.", "\n\nprivate fun Logger.")
                  .replace(
                     "*/\nabstract class EmbeddedKotlinPlugin",
                     "*/\ninternal abstract class EmbeddedKotlinPlugin"
                  )
            )
         }
      }
   }
}

sourceSets {
   main {
      kotlin.srcDir(fixGradlePluginWarning)
   }
}
//endregion
