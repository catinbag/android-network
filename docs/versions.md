# Versions

## Libraries

| Library | Artifact | Version | Source of the check |
|---|---|---|---|
| Retrofit | `com.squareup.retrofit2:retrofit` | 2.11.0 | POM of `retrofit-2.11.0` (author's `curl`) |
| Retrofit converter | `com.squareup.retrofit2:converter-gson` | 2.11.0 (same line in `libs.versions.toml`) | POM: brings Gson 2.10.1 |
| OkHttp | brought by Retrofit | 3.14.9 | POM of `retrofit-2.11.0` |
| OkHttp logging | `com.squareup.okhttp3:logging-interceptor` | 3.14.9 | Same as OkHttp |
| MockWebServer | `com.squareup.okhttp3:mockwebserver` | 3.14.9 | Same as OkHttp |
| Gson | brought by `converter-gson` | 2.10.1 | POM of `converter-gson-2.11.0`. Not pinned apart |
| Glide | `com.github.bumptech.glide:glide` | 4.16.0 | Exists in `maven-metadata.xml`. The latest of all is 5.0.9; 4.x is used |
| Picasso | `com.squareup.picasso:picasso` | 2.8 | The author built the module, so it resolves. Not checked as "the latest" |
| AndroidX | `appcompat` 1.6.1, `material` 1.10.0, `recyclerview` 1.3.1, `activity` 1.8.0, `constraintlayout` 2.1.4 | as in `android-lists` | |
| JUnit | `junit:junit` | 4.13.2 | |

Why Retrofit 2.11.0 and not 2.9.0: both bring OkHttp 3.14.9. 2.11.0 brings Gson 2.10.1 and 2.9.0 brings Gson 2.8.5 (both read from the POMs). Both are built for Java 8 and need no Kotlin runtime. OkHttp 3.14.9 is written in Java, so the project has no `kotlin-stdlib` of its own.

Check of the dependencies after a change: `./gradlew :retrofit-basic:dependencies`.

## Gradle, AGP, JDK

| Branch | Gradle | AGP | `compileSdk` | Status |
|---|---|---|---|---|
| `master` | 8.4 | 8.3.1 | 34 | Used. `:core:test` ran OK (author). The modules were built and run by the author |
| `compat/agp-8.1.1` | 8.0 | 8.1.1 | 34 | To create by the author (not in this repository yet) |
| `compat/agp-7.4` | 7.5 | 7.4.2 | 33 | To create by the author (not in this repository yet) |

This is the same set as `master` and the branches of `android-lists` and `android-patterns`.

Common settings: `minSdk` 26, Java source and target 1.8, JDK 17, Kotlin DSL, `gradle/libs.versions.toml`.

What differs in `compat/agp-8.1.1` (by `android-lists`): `agp` in `libs.versions.toml` and `distributionUrl` in `gradle-wrapper.properties`.

What `android-lists` needed in `compat/agp-7.4` (not checked for this project):
- `compileSdk` and `targetSdk` 34 → 33 in every module (and the `material` 1.10.0 → 1.9.0, `activity` 1.8.0 → 1.7.2 pair, because `material` 1.10.0 brings `activity` 1.8.0, which needs `compileSdk` 34);
- remove `EdgeToEdge.enable(this)` and its import in `app/…/MainActivity.java` (the class is in `activity` 1.8.0 and later; the wizard `MainActivity` uses it);
- a `subprojects` block with `kotlin-stdlib-jdk7` and `jdk8` forced to 1.8.10 (duplicate classes). This project may not need it: it has no Kotlin of its own. Not checked;
- not checked here: Glide 4.16.0 and Picasso 2.8 with `compileSdk` 33.

## JDK in Android Studio

The IDE brings its own JDK (a recent Android Studio: Java 25). Gradle 8.4 runs on Java 8–20. If the IDE says "The project's Gradle version 8.4 is incompatible with the Gradle JVM version 25", select JDK 17 in Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK, and in File → Project Structure → SDK. The same setting is in `android-lists` (`.idea` is not in git).

The new-project wizard of this IDE makes AGP 9.4.1, Gradle 9.6.0 and `compileSdk` 37. Do not accept updates: the classroom needs Gradle 8.1, 8.3 or 8.4.

Max Java for running Gradle: 7.5 → 18, 8.0 → 19, 8.4 → 20. JDK 17 fits all three branches.

## Not checked

- A build on `compat/agp-8.1.1` and `compat/agp-7.4` of this project.
- A build and run on the classroom computers.
