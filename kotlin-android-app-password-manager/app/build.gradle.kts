plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ru.rbkdev.passwordmanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.rbkdev.passwordmanager"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    implementation(fileTree(
        mapOf(
            "dir" to "C:/PasswordManager/resources/library",
            "include" to listOf(
                "cloud-lib.jar",
                "commons-logging-1.2.jar",
                "httpclient-4.5.13.jar",
                "httpcore-4.4.16.jar",
                "xmlpull-1.1.3.4a.jar"
            )
        )))

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}

tasks.register("copy") {
    copy {
        from("C:/PasswordManager/resources")
        into("C:/PasswordManager/kotlin-android-app-password-manager/app/src/main/res/raw")
        include("salt", "iv", "token")
    }
}

tasks.preBuild {
    dependsOn("copy")
}