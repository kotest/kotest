apply(plugin = "java")
apply(plugin = "java-library")
apply(plugin = "org.jetbrains.dokka")

apply(from = "$rootDir/signing-pom-details.gradle.kts")


//val dokka = tasks.named("dokka")
val javadoc = tasks.named("javadoc")

// Create dokka Jar task from dokka task output

//val dokkaJar by tasks.creating(Jar::class) {
//   group = JavaBasePlugin.DOCUMENTATION_GROUP
//   description = "Assembles Kotlin docs with Dokka"
//   archiveClassifier.set("javadoc")
//   from(dokka)
//}
val javadocJar by tasks.creating(Jar::class) {
   group = JavaBasePlugin.DOCUMENTATION_GROUP
   description = "Assembles java doc to jar"
   archiveClassifier.set("javadoc")
   from(javadoc)
}

fun Project.publishing(action: PublishingExtension.() -> Unit) =
   configure(action)

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

publishing {
   publications.withType<MavenPublication>().forEach {
      it.apply {
         //if (Ci.isRelease)
         artifact(javadocJar)
      }
   }
}
