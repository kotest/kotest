import gradle.kotlin.dsl.accessors._7b9f36e796b9a89b80b1048d46b5db5b.commonMainImplementation

plugins {
   signing
   `maven-publish`
}

val javadocJar by tasks.registering(Jar::class) {
   group = JavaBasePlugin.DOCUMENTATION_GROUP
   description = "Empty Javadoc Jar (required by Maven Central)"
   archiveClassifier.set("javadoc")
}

val ossrhUsername: String by project
val ossrhPassword: String by project
val signingKey: String? by project
val signingPassword: String? by project

val mavenInternalDir = layout.buildDirectory.file("project-maven-internal")

publishing {
   repositories {
      maven {
         name = "deploy"
         val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
         val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
         url = uri(if (Ci.isRelease) releasesRepoUrl else snapshotsRepoUrl)
         credentials {
            username = System.getenv("OSSRH_USERNAME") ?: ossrhUsername
            password = System.getenv("OSSRH_PASSWORD") ?: ossrhPassword
         }
      }

      maven(mavenInternalDir) {
         name = "ProjectInternal"
      }
   }

   publications.withType<MavenPublication>().configureEach {
      artifact(javadocJar)

      pom {
         name.set("Kotest")
         description.set("Kotlin Test Framework")
         url.set("https://github.com/kotest/kotest")

         scm {
            connection.set("scm:git:https://github.com/kotest/kotest/")
            developerConnection.set("scm:git:https://github.com/sksamuel/")
            url.set("https://github.com/kotest/kotest/")
         }

         licenses {
            license {
               name.set("Apache-2.0")
               url.set("https://opensource.org/licenses/Apache-2.0")
            }
         }

         developers {
            developer {
               id.set("sksamuel")
               name.set("Stephen Samuel")
               email.set("sam@sksamuel.com")
            }
         }
      }
   }
}

signing {
   useGpgCmd()
   if (signingKey != null && signingPassword != null) {
      useInMemoryPgpKeys(signingKey, signingPassword)
   }
   if (Ci.isRelease) {
      sign(publishing.publications)
   }
}


val mavenInternalPublishTasks = tasks.withType<PublishToMavenRepository>()
   .matching { it.repository.name == "ProjectInternal" }


val mavenInternalZipped by tasks.registering(Zip::class) {
   dependsOn(mavenInternalPublishTasks)
   archiveClassifier.set("maven-internal")
   from(mavenInternalDir)
   destinationDirectory.set(temporaryDir)
}


val mavenInternalProvider by configurations.registering {
   asProvider()
   attributes { mavenInternalAttributes(objects) }
   outgoing {
      artifact(mavenInternalZipped)
   }
}
