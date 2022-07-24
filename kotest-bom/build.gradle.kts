plugins {
   `java-platform`
   `maven-publish`
   signing
}

group = "io.kotest"
version = Ci.publishVersion

val bomProject = project

// Explicitly exclude subprojects that will never be published so that when configuring this project
//   we don't force their configuration and do unecessary work
val excludeFromBom = listOf("kotest-examples", "kotest-tests")
fun projectsFilter(candidateProject: Project) =
   excludeFromBom.all { !candidateProject.name.contains(it) } &&
      candidateProject.name != bomProject.name

// Declare that this subproject depends on all subprojects matching the filter
// When this subproject is configured, it will force configuration of all subprojects
// so that we can declare dependencies on them
rootProject.subprojects.filter(::projectsFilter).forEach { bomProject.evaluationDependsOn(it.path) }

dependencies {
   constraints {
      rootProject.subprojects.filter { project ->
         // Only declare dependencies on projects that will have publications
         projectsFilter(project) && project.tasks.findByName("publish")?.enabled == true
      }.forEach { api(project(it.path)) }
   }
}

val ossrhUsername: String by project
val ossrhPassword: String by project
val signingKey: String? by project
val signingPassword: String? by project

publishing {
   repositories {
      maven {
         val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
         val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
         name = "deploy"
         url = if (Ci.isRelease) releasesRepoUrl else snapshotsRepoUrl
         credentials {
            username = System.getenv("OSSRH_USERNAME") ?: ossrhUsername
            password = System.getenv("OSSRH_PASSWORD") ?: ossrhPassword
         }
      }
   }

   publications {
      create<MavenPublication>("KotestBom") {
         from(components["javaPlatform"])
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
}

signing {
   val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications
   useGpgCmd()

   if (signingKey != null && signingPassword != null) {
      @Suppress("UnstableApiUsage")
      useInMemoryPgpKeys(signingKey, signingPassword)
   }

   if (Ci.isRelease) {
      sign(publications)
   }
}
