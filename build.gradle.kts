// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.vanniktech.maven.publish) apply false
}

tasks.register<Copy>("syncMapHtml") {
    group = "build"
    description = "Syncs canonical map.html from leaflekt-compose to leaflekt-core"
    from("leaflekt-compose/src/commonMain/composeResources/files/map.html")
    into("leaflekt-core/src/iosMain/resources")
}

