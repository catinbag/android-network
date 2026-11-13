plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    // `api`: DogApi exposes Retrofit types, so modules that use core need them too.
    // Gson comes from converter-gson (same version as the converter needs).
    api(libs.retrofit)
    api(libs.retrofit.converter.gson)

    testImplementation(libs.junit)
    testImplementation(libs.okhttp.mockwebserver)
}
