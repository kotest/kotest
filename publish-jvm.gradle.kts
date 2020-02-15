apply(plugin = "java")
apply(plugin = "java-library")
apply(plugin = "maven-publish")

repositories {
   mavenCentral()
}

val ossrhUsername: String by project
val ossrhPassword: String by project

fun Project.publishing(action: PublishingExtension.() -> Unit) =
   configure<PublishingExtension>(action)

publishing {
   publications {
      create<MavenPublication>("mavenJava") {
         from(components["java"])
      }
   }
   repositories {
      maven {
         val releasesRepoUrl = java.net.URI("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
         val snapshotsRepoUrl = java.net.URI("https://oss.sonatype.org/content/repositories/snapshots/")
         name = "deploy"
         url = if (Ci.isGithub) snapshotsRepoUrl else releasesRepoUrl
         credentials {
            username = System.getenv("OSSRH_USERNAME") ?: ossrhUsername
            password = System.getenv("OSSRH_PASSWORD") ?: ossrhPassword
         }
      }
   }
}
