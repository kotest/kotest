buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
   }
}

plugins {
   java
   `java-library`
   kotlin("multiplatform")
   application
   id("com.github.johnrengelman.shadow")
}

apply(plugin = "com.github.johnrengelman.shadow")

kotlin {
   targets {
      jvm()
   }
}

application {
   mainClass.set("io.kotest.engine.launcher.MainKt")
}

tasks {
   jar {
      archiveClassifier.set("default")
   }
   shadowJar {
      archiveClassifier.set(null as String?)
      archiveBaseName.set("kotest-framework-standalone-jvm")
      exclude("**/module-info.class")
      mergeServiceFiles()
      manifest {
         attributes(Pair("Main-Class", "io.kotest.engine.launcher.MainKt"))
      }
   }
   getByName("jvmJar") {
      finalizedBy(getByName("shadowJar"))
   }
}

dependencies {
   implementation(kotlin("stdlib"))
   implementation(kotlin("reflect"))
   implementation(project(Projects.Framework.engine))
}

apply(from = "../../publish-mpp.gradle.kts")
