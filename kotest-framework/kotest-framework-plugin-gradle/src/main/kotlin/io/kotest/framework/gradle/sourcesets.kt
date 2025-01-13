package io.kotest.framework.gradle

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.internal.DefaultJavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.extensibility.DefaultConvention
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetWithTests

fun Project.mppTestTargets(): Map<KotlinTargetWithTests<*, *>, FileCollection> {
   println("exts=" + project.extensions)
   val ext = project.extensions as DefaultConvention
   println("map=" + ext.asMap)

   return when (val kotlin = project.extensions.getByName("kotlin")) {
      is KotlinMultiplatformExtension -> {
         println("Detected mpp kotlin $kotlin")
         println("kotlin.sourceSets ${kotlin.sourceSets.map { it.name }}")
         println("kotlin.targets ${kotlin.targets.map { it.targetName + " " + it.platformType }}")
         println("kotlin.testableTargets ${kotlin.testableTargets.map { it.targetName + " " + it.platformType }}")
         kotlin.testableTargets.filter {
            // kotest plugin only supports JVM
            when (it.platformType) {
               KotlinPlatformType.jvm, KotlinPlatformType.androidJvm -> true
               else -> false
            }
         }.associate { target ->
            println("Detected mpp target $target")
            val deps = target.compilations.map {
               when (it) {
                  is KotlinCompilationToRunnableFiles -> it.runtimeDependencyFiles + it.compileDependencyFiles
                  else -> it.compileDependencyFiles
               }
            }
            val outputs = target.compilations.map { it.output.allOutputs }
            val classpath = (deps + outputs).reduce { a, b -> a.plus(b) }
            Pair(target, classpath)
         }
      }
      is KotlinProjectExtension -> {
         println("kotlin KotlinProjectExtension $kotlin")
         emptyMap()
      }
      else -> emptyMap()
   }
}

fun Project.javaTestSourceSet(): SourceSet? =
   extensions.findByType(JavaPluginExtension::class.java)
      ?.sourceSets
      ?.findByName("test")
