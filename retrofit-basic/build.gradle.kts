plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "org.demo.network.retrofitbasic"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.demo.network.retrofitbasic"
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

dependencies {
    implementation(project(":core"))   // brings Retrofit, converter-gson, OkHttp (api)
    implementation(libs.okhttp.logging)
    implementation(libs.appcompat)
    implementation(libs.material)
}
