package io.github.ikarenkov.kombucha

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import java.util.Properties

internal fun Project.setupPublishing() {
    readEnvironmentVariables()

    val javadocJar = tasks.register<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
    }

    val isSigningEnabled = getExtraString("signing.keyId") != null

    if (isSigningEnabled) {
        // Workaround for https://github.com/gradle/gradle/issues/26091 and https://youtrack.jetbrains.com/issue/KT-46466
        val signingTasks = tasks.withType<Sign>()
        tasks.withType<AbstractPublishToMaven>().configureEach {
            dependsOn(signingTasks)
        }
    }

    configure<PublishingExtension> {
        if (isSigningEnabled) {
            configure<SigningExtension> {
                // Signing artifacts. Signing.* extra properties values will be used
                sign(publications)
            }
        }
        // Configure maven central repository
        repositories {
            maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
                name = "sonatype"
                credentials {
                    username = getExtraString("sonatypeUsername")
                    password = getExtraString("sonatypePassword")
                }
            }
        }

        // Configure all publications
        publications.withType<MavenPublication> {
            groupId = "io.github.ikarenkov"
            withVersionCatalog { libs ->
                version = libs.versions.kombucha.get().toString()
            }

            artifact(javadocJar.get())

            // Provide artifacts information requited by Maven Central
            pom {
                name = "Kombucha UDF"
                description =
                    "UDF library based on The Elm Architecture (TEA) concepts that helps to focus on logic of you application, " +
                            "rather then understanding what is going on."
                url = "https://github.com/ikarenkov/kombucha-udf"

                setupLicense()
                setupDevelopers()
                scm {
                    url = "https://github.com/ikarenkov/kombucha-udf"
                }
            }
        }
    }

    val publications = extensions.findByType<PublishingExtension>()?.publications

    afterEvaluate {
        publications?.withType<MavenPublication> {
            artifactId = "kombucha-$artifactId"
        }
    }
}

private fun MavenPom.setupDevelopers() {
    developers {
        developer {
            id = "ikarenkov"
            name = "Igor Karenkov"
            email = "karenkovigor@gmail.com"
        }
    }
}

private fun MavenPom.setupLicense() {
    licenses {
        license {
            name.set("MIT")
            url.set("https://opensource.org/licenses/MIT")
        }
    }
}

private fun Project.readEnvironmentVariables() {
    extra["signing.keyId"] = null
    extra["signing.password"] = null
    extra["signing.secretKeyRingFile"] = null
    extra["sonatypeUsername"] = null
    extra["sonatypePassword"] = null

// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
    val secretPropsFile = project.rootProject.file("local.properties")
    if (secretPropsFile.exists()) {
        secretPropsFile.reader().use {
            Properties().apply { load(it) }
        }.onEach { (name, value) ->
            extra[name.toString()] = value
        }
        extra["signing.secretKeyRingFile"] = project.rootProject.layout.projectDirectory.file(extra["signing.secretKeyRingFile"].toString())
    } else {
        extra["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
        extra["signing.password"] = System.getenv("SIGNING_PASSWORD")
        extra["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
        extra["sonatypeUsername"] = System.getenv("SONATYPE_USERNAME")
        extra["sonatypePassword"] = System.getenv("SONATYPE_PASSWORD")
    }
}

private fun Project.getExtraString(name: String) = extra[name]?.toString()
