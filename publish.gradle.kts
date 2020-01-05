//publishing {
//   repositories {
//      maven {
//
//         val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
//         val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
//
//         val ossrhUsername: String by project
//         val ossrhPassword: String by project
//
//         name = "deploy"
//         url = if (Travis.isTravis) snapshotsRepoUrl else releasesRepoUrl
//         credentials {
//            username = System.getenv("OSSRH_USERNAME") ?: ossrhUsername
//            password = System.getenv("OSSRH_PASSWORD") ?: ossrhPassword
//         }
//      }
//   }
//}

//    publications {
//        mavenJava(MavenPublication) {
//
//            from components.java
//
//            if (isReleaseVersion) {
//                artifact sourcesJar
//                artifact javadocJar
//            }
//
//            pom {
//                name = 'Kotest'
//                description = 'Kotlin Test Framework'
//                url = 'http://www.github.com/kotlintest/kotlintest'
//
//                scm {
//                    connection = 'scm:git:http://www.github.com/kotlintest/kotlintest/'
//                    developerConnection = 'scm:git:http://github.com/sksamuel/'
//                    url = 'http://www.github.com/kotlintest/kotlintest/'
//                }
//
//                licenses {
//                    license {
//                        name = 'The Apache 2.0 License'
//                        url = 'https://opensource.org/licenses/Apache-2.0'
//                    }
//                }
//
//                developers {
//                    developer {
//                        id = 'sksamuel'
//                        name = 'Stephen Samuel'
//                        email = 'sam@sksamuel.com'
//                    }
//                }
//            }
//        }
//    }

//artifacts {
//    archives javadocJar, sourcesJar
//}
//
//build {
//    // afterReleaseBuild.dependsOn publish
//}
//
//tasks.withType(Sign) {
//    onlyIf { isReleaseVersion }
//}
//
//tasks.withType(Javadoc) {
//    onlyIf { isReleaseVersion }
//}
//
//signing {
//    useGpgCmd()
//    sign publishing.publications.mavenJava
//}
