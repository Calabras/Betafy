plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.betafy.feature.notification.di"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:35.0.0"))
    implementation("com.google.firebase:firebase-database")
    implementation(project(":feature:notification:domain"))
    implementation(project(":feature:notification:data"))
    implementation(project(":feature:notification:presentation"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
