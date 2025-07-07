plugins {
   id("com.gradleup.nmcp.aggregation")
}

nmcpAggregation {
   centralPortal {
      // New secrets need to be generated at https://central.sonatype.com/account w/ Generate User Token
      username.set(System.getenv("NEW_MAVEN_CENTRAL_USERNAME"))
      password.set(System.getenv("NEW_MAVEN_CENTRAL_PASSWORD"))
      publishingType = "AUTOMATIC"
   }
}

val publishToAppropriateCentralRepository by tasks.registering {
   group = "publishing"
   if (Ci.isRelease) {
      dependsOn(tasks.named("publishAggregationToCentralPortal"))
   } else {
      dependsOn(tasks.named("publishAggregationToCentralPortalSnapshots"))
   }
}
