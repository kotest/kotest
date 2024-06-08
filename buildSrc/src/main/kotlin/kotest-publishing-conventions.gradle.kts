plugins {
   signing
   `maven-publish`
}

group = "io.kotest"
version = Ci.publishVersion

val javadocJar by tasks.registering(Jar::class) {
   group = JavaBasePlugin.DOCUMENTATION_GROUP
   description = "Assembles java doc to jar"
   archiveClassifier.set("javadoc")
   val javadoc = tasks.named("javadoc")
   from(javadoc)
}

val ossrhUsername: String by project
val ossrhPassword: String by project
val signingKey: String? by project
val signingPassword: String? by project

signing {
   useGpgCmd()
   if (signingKey != null && signingPassword != null) {
      useInMemoryPgpKeys(signingKey, signingPassword)
   }
   if (Ci.isRelease) {
      sign(publishing.publications)
   }
}

publishing {
   repositories {
      maven {
         val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
         val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
         name = "deploy"
         url = if (Ci.isRelease) releasesRepoUrl else snapshotsRepoUrl
         credentials {
            username = System.getenv("OSSRH_USERNAME") ?: ossrhUsername
            password = System.getenv("OSSRH_PASSWORD") ?: ossrhPassword
         }
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

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
   publishPlatformArtifactsInRootModule(project)
}

//region Maven Central can't handle parallel uploads, so limit parallel uploads with a BuildService
abstract class MavenPublishLimiter : BuildService<BuildServiceParameters.None>

val mavenPublishLimiter =
   gradle.sharedServices.registerIfAbsent("mavenPublishLimiter", MavenPublishLimiter::class) {
      maxParallelUsages = 1
   }

tasks.withType<PublishToMavenRepository>().configureEach {
   usesService(mavenPublishLimiter)
}
//endregion
