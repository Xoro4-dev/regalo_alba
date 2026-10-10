plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

val releaseSigningPropertyNames = listOf(
    "PLAY_UPLOAD_STORE_FILE",
    "PLAY_UPLOAD_STORE_PASSWORD",
    "PLAY_UPLOAD_KEY_ALIAS",
    "PLAY_UPLOAD_KEY_PASSWORD",
)
val releaseSigningProperties = releaseSigningPropertyNames.associateWith { name ->
    providers.gradleProperty(name)
        .orElse(providers.environmentVariable(name))
        .orNull
}
val releaseSigningConfigured = releaseSigningPropertyNames.all { name ->
    !releaseSigningProperties[name].isNullOrBlank()
}

android {
    namespace = "com.hugodev.horasconamor"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.hugodev.horasconamor"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        if (releaseSigningConfigured) {
            create("release") {
                storeFile = file(requireNotNull(releaseSigningProperties["PLAY_UPLOAD_STORE_FILE"]))
                storePassword = requireNotNull(releaseSigningProperties["PLAY_UPLOAD_STORE_PASSWORD"])
                keyAlias = requireNotNull(releaseSigningProperties["PLAY_UPLOAD_KEY_ALIAS"])
                keyPassword = requireNotNull(releaseSigningProperties["PLAY_UPLOAD_KEY_PASSWORD"])
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (releaseSigningConfigured) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

val verifyReleaseSigning = tasks.register("verifyReleaseSigning") {
    doLast {
        check(releaseSigningConfigured) {
            """
            Release signing is not configured. Set PLAY_UPLOAD_STORE_FILE,
            PLAY_UPLOAD_STORE_PASSWORD, PLAY_UPLOAD_KEY_ALIAS, and PLAY_UPLOAD_KEY_PASSWORD
            in the environment or your user-level Gradle properties before building a release.
            """.trimIndent()
        }
    }
}

tasks.configureEach {
    if (name == "bundleRelease" || name == "assembleRelease") {
        dependsOn(verifyReleaseSigning)
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
}
