plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.betafy.feature.match.data"
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
    implementation("com.google.firebase:firebase-database")
    implementation(platform("com.google.firebase:firebase-bom:35.0.0"))
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(project(":feature:match:domain"))
    testImplementation(libs.junit)
}
