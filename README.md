#Name of jar
tasks.named("bootJar", org.gradle.jvm.tasks.Jar::class) {
    archiveFileName.set("bus_core.jar")
    destinationDirectory.set(project.rootDir)
}