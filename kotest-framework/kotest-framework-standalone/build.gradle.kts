import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
   id("kotest-jvm-conventions")
   alias(libs.plugins.shadowjar)
}

kotlin {
   jvm {
      @OptIn(ExperimentalKotlinGradlePluginApi::class)
      binaries {
         executable {
            mainClass.set("io.kotest.engine.launcher.MainKt")
         }
      }
   }
}

tasks {
   val shadowJar = withType<ShadowJar> {
      archiveClassifier.set(null as String?)
      archiveBaseName.set("kotest-framework-standalone-jvm")
      exclude("**/module-info.class")
      mergeServiceFiles()
      manifest {
         attributes(Pair("Main-Class", "io.kotest.engine.launcher.MainKt"))
      }
   }
   getByName("jvmJar") {
      finalizedBy(shadowJar)
   }

   withType<CreateStartScripts> {
      dependsOn(shadowJar)
   }
}

dependencies {
   implementation(libs.kotlin.reflect)
   implementation(projects.kotestFramework.kotestFrameworkEngine)
}
