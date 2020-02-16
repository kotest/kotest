apply(plugin = "java")
apply(plugin = "java-library")
apply(plugin = "maven-publish")
apply(plugin = "signing")
apply(plugin = "org.jetbrains.dokka")

repositories {
   mavenCentral()
}

val ossrhUsername: String by project
val ossrhPassword: String by project

fun Project.publishing(action: PublishingExtension.() -> Unit) =
   configure<PublishingExtension>(action)

publishing {
   repositories {
      maven {
         val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
         val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
         name = "deploy"
         url = if (Ci.isGithub) snapshotsRepoUrl else releasesRepoUrl
         credentials {
            username = System.getenv("OSSRH_USERNAME") ?: ossrhUsername
            password = System.getenv("OSSRH_PASSWORD") ?: ossrhPassword
         }
      }
   }
}

tasks.create<Jar>("sourcesJar") {
   archiveClassifier.set("sources")
   //from(sourceSets.main.all)
}

tasks.create<Jar>("javadocJar") {
   archiveClassifier.set("javadoc")
}
