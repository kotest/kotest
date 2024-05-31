import groovy.util.Node
import groovy.util.NodeList
import org.gradle.configurationcache.extensions.capitalized

/**
 * Publish the platform JAR and POM so that consumers who depend on this module and can't read Gradle module
 * metadata can still get the platform artifact and transitive dependencies from the POM
 * (see details in https://youtrack.jetbrains.com/issue/KT-39184#focus=streamItem-27-4115233.0-0)
 */
fun Project.publishPlatformArtifactsInRootModule() {
   val platformPublication: MavenPublication? =
      extensions
         .findByType(PublishingExtension::class.java)
         ?.publications
         ?.getByName<MavenPublication>("jvm")
   if (platformPublication != null) {

      lateinit var platformXml: XmlProvider
      platformPublication.pom?.withXml { platformXml = this }

      extensions
         .findByType(PublishingExtension::class.java)
         ?.publications
         ?.getByName("kotlinMultiplatform")
         ?.let { it as MavenPublication }
         ?.run {

            // replace pom
            pom.withXml {
               val xmlProvider = this
               val root = xmlProvider.asNode()
               // Remove the original content and add the content from the platform POM:
               root.children().toList().forEach { root.remove(it as Node) }
               platformXml.asNode().children().forEach { root.append(it as Node) }

               // Adjust the self artifact ID, as it should match the root module's coordinates:
               ((root.get("artifactId") as NodeList).get(0) as Node).setValue(artifactId)

               // Set packaging to POM to indicate that there's no artifact:
               root.appendNode("packaging", "pom")

               // Remove the original platform dependencies and add a single dependency on the platform
               // module:
               val dependencies = (root.get("dependencies") as NodeList).get(0) as Node
               dependencies.children().toList().forEach { dependencies.remove(it as Node) }
               val singleDependency = dependencies.appendNode("dependency")
               singleDependency.appendNode("groupId", platformPublication.groupId)
               singleDependency.appendNode("artifactId", platformPublication.artifactId)
               singleDependency.appendNode("version", platformPublication.version)
               singleDependency.appendNode("scope", "compile")
            }
         }

      tasks
         .matching { it.name == "generatePomFileForKotlinMultiplatformPublication" }
         .configureEach {
            dependsOn("generatePomFileFor${platformPublication.name.capitalized()}Publication")
         }
   }
}

plugins {
   signing
   `maven-publish`
}

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

val javadoc = tasks.named("javadoc")

group = "io.kotest"
version = Ci.publishVersion

val javadocJar by tasks.creating(Jar::class) {
   group = JavaBasePlugin.DOCUMENTATION_GROUP
   description = "Assembles java doc to jar"
   archiveClassifier.set("javadoc")
   from(javadoc)
}


publishing {
   publications.withType<MavenPublication>().forEach {
      it.apply {
         artifact(javadocJar)
      }
   }
}

val ossrhUsername: String by project
val ossrhPassword: String by project
val signingKey: String? by project
val signingPassword: String? by project

signing {
   useGpgCmd()
   if (signingKey != null && signingPassword != null) {
      @Suppress("UnstableApiUsage")
      useInMemoryPgpKeys(signingKey, signingPassword)
   }
   if (Ci.isRelease) {
      sign(publications)
   }
}

publishPlatformArtifactsInRootModule()

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


