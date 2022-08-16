import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
   kotlin("jvm")
   `maven-publish`
   `java-gradle-plugin`
   `kotlin-dsl`
   alias(libs.plugins.gradle.plugin.publish)
   `jvm-test-suite`
}

group = "io.kotest"
version = Ci.gradleVersion

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
   mavenCentral()
   mavenLocal()
}


val mavenInternal by configurations.creating<Configuration> {
   asConsumer()
   attributes { mavenInternalAttributes(objects) }
}
val mavenInternalDir = layout.buildDirectory.dir("maven-internal")

dependencies {
   implementation(libs.kotlin.gradle.plugin)

   mavenInternal(project(Projects.Assertions.Api))
   mavenInternal(project(Projects.Assertions.Core))
   mavenInternal(project(Projects.Assertions.Shared))
   mavenInternal(project(Projects.Common))
   mavenInternal(project(Projects.Extensions))
   mavenInternal(project(Projects.Framework.api))
   mavenInternal(project(Projects.Framework.concurrency))
   mavenInternal(project(Projects.Framework.discovery))
   mavenInternal(project(Projects.Framework.engine))
   mavenInternal(project(Projects.JunitRunner))
}

val kotlinGeneratedSrcDir: DirectoryProperty = objects.directoryProperty()
   .convention(layout.buildDirectory.dir("generated/src/main/kotlin/"))

sourceSets.main {
   java.srcDir(kotlinGeneratedSrcDir)
}


pluginBundle {
   website = "https://kotest.io"
   vcsUrl = "https://github.com/kotest"
   tags = listOf("kotest", "kotlin", "testing", "integrationTesting", "javascript")
}


gradlePlugin {
   plugins {
      create("KotestMultiplatformCompilerGradlePlugin") {
         id = "io.kotest.multiplatform"
         implementationClass = "io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePlugin"
         displayName = "Kotest Multiplatform Compiler Plugin"
         description = "Adds support for Javascript and Native tests in Kotest"
      }
   }
}


val updateKotestPluginConstants by tasks.registering {
   val kotestConstantsFileContent: String = """
            |// Do not edit manually. This file was created by ${this.path}
            |
            |package io.kotest.framework.multiplatform.gradle
            |
            |const val KOTEST_COMPILER_PLUGIN_VERSION: String = "${Ci.gradleVersion}"
            |
         """.trimMargin()
   inputs.property("kotestConstantsFileContent", kotestConstantsFileContent)

   val kotestConstantsOutputFile = kotlinGeneratedSrcDir.file(
      "io/kotest/framework/multiplatform/gradle/kotestPluginConstants.kt"
   )
   outputs.file(kotestConstantsOutputFile)

   doLast {
      logger.lifecycle("Updating Kotest Gradle plugin constants\n\n${kotestConstantsFileContent.prependIndent("  > ")}\n")
      kotestConstantsOutputFile.get().asFile.writeText(
         kotestConstantsFileContent.lines().joinToString("\n")
      )
   }
}

tasks.withType<KotlinCompile>().configureEach {
   dependsOn(updateKotestPluginConstants)
}


tasks.assemble {
   dependsOn(updateKotestPluginConstants)
}


tasks.clean {
   delete("$projectDir/test-project/build/")
   delete("$projectDir/test-project/.gradle/")
}


@Suppress("UnstableApiUsage") // jvm test suites are incubating
testing.suites {
   val test by getting(JvmTestSuite::class) {
      useJUnitJupiter()

      dependencies {
         implementation(project(Projects.Assertions.Core))
         implementation(project(Projects.Framework.api))
         implementation(project(Projects.Framework.engine))
         implementation(project(Projects.JunitRunner))

         implementation(libs.mockk)
      }
   }

   val functionalTest by registering(JvmTestSuite::class) {
      useJUnitJupiter()

      dependencies {
         implementation(project)

         implementation(project.dependencies.gradleTestKit())
         implementation(project(Projects.Assertions.Core))
         implementation(project(Projects.Framework.api))
         implementation(project(Projects.Framework.engine))
         implementation(project(Projects.JunitRunner))
      }

      targets.all {
         testTask.configure {
            shouldRunAfter(test)
            dependsOn(installMavenInternal)
            systemProperty("mavenInternalDir", file(mavenInternalDir).canonicalPath)
         }
      }

      sources {
         java {
            resources {
               srcDir(tasks.pluginUnderTestMetadata.map { it.outputDirectory })
            }
         }
      }

      gradlePlugin.testSourceSet(sources)
   }

   tasks.check { dependsOn(functionalTest) }
}


interface Services {
   @get:Inject
   val softwareComponents: SoftwareComponentFactory
   @get:Inject
   val files: FileSystemOperations
}

val services = objects.newInstance(Services::class)


val installMavenInternal by tasks.registering {
   dependsOn(mavenInternal)
   group = LifecycleBasePlugin.BUILD_GROUP

   outputs.dir(mavenInternalDir)

   doFirst {
      services.files.delete { delete(mavenInternalDir) }

      mavenInternal
         .incoming
//         .artifactView { lenient(true) }
         .files.forEach { file ->
            services.files.copy {
               from(zipTree(file))
               into(mavenInternalDir)
            }
         }
   }
}
