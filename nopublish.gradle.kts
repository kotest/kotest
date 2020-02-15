apply(plugin = "java")
apply(plugin = "java-library")
apply(plugin = "maven-publish")

fun Project.publishing(action: PublishingExtension.() -> Unit) =
   configure<PublishingExtension>(action)

tasks.named("publish") {
   enabled = false
}

tasks.named("publishToMavenLocal") {
   enabled = false
}
