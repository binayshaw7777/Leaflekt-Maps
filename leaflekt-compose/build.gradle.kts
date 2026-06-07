import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.maven.publish)
}

val releaseVersion = rootProject.file("VERSION").readText().trim()

group = "io.github.binayshaw7777"
version = releaseVersion

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release")
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api(project(":leaflekt-core"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(libs.kotlinx.coroutines.core)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.webkit)
            implementation(libs.google.play.services.location)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "com.binayshaw7777.leaflekt.compose"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].assets.srcDirs("src/androidMain/assets")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
}

mavenPublishing {
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = true,
            androidVariantsToPublish = listOf("release"),
        )
    )
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    if (System.getenv("CI") != null) {
        signAllPublications()
    }

    coordinates("io.github.binayshaw7777", "leaflekt-compose", releaseVersion)

    pom {
        name.set("LeafleKT Compose Multiplatform")
        description.set("Compose Multiplatform (Android + iOS) wrapper around Leaflet.js.")
        url.set("https://github.com/binayshaw7777/LeafleKT")
        inceptionYear.set("2024")

        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }

        developers {
            developer {
                id.set("binayshaw7777")
                name.set("Binay Shaw")
                url.set("https://github.com/binayshaw7777")
            }
        }

        scm {
            url.set("https://github.com/binayshaw7777/LeafleKT")
            connection.set("scm:git:https://github.com/binayshaw7777/LeafleKT.git")
            developerConnection.set("scm:git:ssh://git@github.com/binayshaw7777/LeafleKT.git")
        }
    }
}
