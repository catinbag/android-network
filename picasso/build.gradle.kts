plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "org.demo.network.picasso"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.demo.network.picasso"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        // BuildConfig.DEBUG: the request log is for debug builds only
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":core"))   // the list of photo URLs; brings Retrofit, Gson, OkHttp (api)
    implementation(libs.picasso)
    implementation(libs.okhttp.logging)
    implementation(libs.recyclerview)
    implementation(libs.appcompat)
    implementation(libs.material)
}
