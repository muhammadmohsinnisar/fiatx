plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
    id("maven-publish")
    id("signing")
}

android {
    buildFeatures {
        buildConfig = true
    }

    namespace = "com.mohsin.fiatx"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mohsin.fiatx"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Production-ready configurations
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
        
        // Localization support - using androidResources for modern approach
        // resourceConfigurations += listOf("en", "es", "fr", "de", "ar", "hi", "zh")

        buildConfigField(
            "String",
            "CURRENCY_API_BASE_URL",
            "\"https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/\""
        )

        buildConfigField(
            "String",
            "CURRENCY_API_FALLBACK_URL",
            "\"https://latest.currency-api.pages.dev/v1/\""
        )
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }
        
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Production signing config (will be configured via CI/CD)
            // signingConfig = signingConfigs.getByName("release")
        }
    }
    
    // Signing configurations for production
    signingConfigs {
        create("release") {
            // These will be provided via environment variables in CI/CD
            storeFile = file(System.getenv("KEYSTORE_FILE") ?: "release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.logging.interceptor)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// Publishing configuration
publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.mohsin.fiatx"
            artifactId = "fiatx-android"
            version = android.defaultConfig.versionName
            
            pom {
                name.set("FiatX")
                description.set("Lightweight currency converter with real-time exchange rates")
                url.set("https://github.com/muhammadmohsinnisar/fiatx")
                
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                
                developers {
                    developer {
                        id.set("mohsinnisar")
                        name.set("Mohsin Nisar")
                        email.set("mohsinnisarbutt60@gmail.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/muhammadmohsinnisar/fiatx.git")
                    developerConnection.set("scm:git:ssh://github.com:muhammadmohsinnisar/fiatx.git")
                    url.set("https://github.com/muhammadmohsinnisar/fiatx/tree/main")
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/muhammadmohsinnisar/fiatx")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}
