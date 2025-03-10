/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.5.0"
}

buildscript {
    repositories {
        mavenCentral()
        google()

        gradlePluginPortal()
    }

    dependencies {
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.4.20")

        classpath(":mvvm-build-logic")
    }
}

allprojects {
    apply(plugin = "org.jetbrains.dokka")

    plugins.withId("org.gradle.maven-publish") {
        group = "dev.icerock.moko"
        version = libs.versions.mokoMvvmVersion.get()
    }
}

val sampleProjects: Set<Project> = project(":sample").allprojects

apiValidation {
    ignoredPackages.add("dev.icerock.moko.mvvm.internal")

    ignoredProjects.addAll(sampleProjects.map { it.name })
}

tasks.register("clean", Delete::class).configure {
    group = "build"
    delete(rootProject.buildDir)
}

tasks.withType<org.jetbrains.dokka.gradle.DokkaMultiModuleTask>().all {
    removeChildTasks(sampleProjects.plus(project(":mvvm")))

    doLast {
        val dir = outputDirectory.get()
        val from = File(dir, "-modules.html")
        val to = File(dir, "index.html")

        from.renameTo(to)

        dir.renameTo(file("docs"))
    }
}
