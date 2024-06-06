plugins {
   `java-platform`
   `maven-publish`
   signing
}

group = "io.kotest"
version = Ci.publishVersion

val bomProject = project

// Explicitly exclude subprojects that will never be published so that when configuring this project
//   we don't force their configuration and do unnecessary work
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
      rootProject.subprojects
         .filter { project ->
            // Only declare dependencies on projects that will have publications
            projectsFilter(project) && project.tasks.findByName("publish")?.enabled == true
         }
         .forEach { project ->
            project.publishing.publications
               .withType<MavenPublication>()
               // Skip platform artifacts (like *-linuxx64, *-macosx64)
               // It leads to inconsistent bom when publishing from different platforms
               // (e.g. on linux it will include only linuxx64 artifacts and no macosx64)
               // It shouldn't be a problem as usually consumers need to use generic *-native artifact
               // Gradle will choose correct variant by using metadata attributes
               .matching { publication -> publication.artifacts.none { it.extension == "klib" } }
               .all {
                  api("${groupId}:${artifactId}:${version}")
               }
         }
   }
}

val ossrhUsername: String by project
val ossrhPassword: String by project
val signingKey: String? by project
val signingPassword: String? by project

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
   useGpgCmd()
   if (signingKey != null && signingPassword != null) {
      useInMemoryPgpKeys(signingKey, signingPassword)
   }
   if (Ci.isRelease) {
      sign(publishing.publications)
   }
}
