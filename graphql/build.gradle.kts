plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "org.demo.network.graphql"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.demo.network.graphql"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        // BuildConfig.DEBUG: the BODY log level is for debug builds only
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// GraphQL is a plain POST, so this module needs only Retrofit. It does not use core.
dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.appcompat)
    implementation(libs.material)
}
