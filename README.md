# Day By Day — Newly Android APK project

Native Android Java application. The original app source and Gradle configuration are retained.

## Upload to Newly

1. Upload this ZIP to Newly's APK builder.
2. Choose the native Java/Kotlin Android project flow.
3. Enter this exact application/bundle identifier: `com.naveen.app`.
4. Gradle is explicitly pinned to **8.9** in `gradle/wrapper/gradle-wrapper.properties`.
5. Start the cloud build and download the signed APK only after Newly reports success.

Newly's upload instructions say it can restore missing Gradle wrapper runtime files when the Gradle version is unambiguous. This ZIP includes the wrapper scripts and properties; the wrapper JAR is not included because it could not be retrieved in this environment.

## Build versions

- Gradle: 8.9
- Android Gradle Plugin: 8.7.3
- compileSdk / targetSdk: 35
- minSdk: 26
- Application ID: `com.naveen.app`
- versionCode: 2
- versionName: 1.1

## Features

- Multiple daily challenges, each 1–365 days.
- Day status cycles grey (unmarked) → green (done) → red (missed) → grey.
- Completion counts and percentages.
- Edit, archive, and delete challenges.
- Light and dark themes.
- Optional daily local reminders.
- Local-only challenge storage.

## Verification status

The ZIP structure and required project files were checked. An Android APK was not compiled in this environment. Newly must complete the cloud build to verify its own image/toolchain preparation and APK signing stages.


## Newly upload note

Enter `com.naveen.app` in Newly's Bundle Identifier field; it matches the app module's
`applicationId`. The Java package/Android namespace remains `com.daybyday.app`, which is
valid and intentionally unchanged.

If Newly's log reports `curl: (22)` while downloading
`/workspace/.newly-tools/node.tar.xz` during `imagePreparation` or `toolchain`, that is a
Newly build-environment download failure, not a Java/Gradle source compilation error.
No project ZIP can repair a failed download from Newly's own toolchain host. Retry after
the service can retrieve that file, or send the full log to Newly support.
