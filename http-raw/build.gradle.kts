plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "org.demo.network.httpraw"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.demo.network.httpraw"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":core"))   // only the DTO and Gson (no Retrofit code is used here)
    implementation(libs.appcompat)
    implementation(libs.material)
}
