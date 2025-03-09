import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelightPlugin)
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui.tooling)
            implementation(libs.compose.foundation)
            implementation(libs.androidx.runtime.android)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            // Serialization
            implementation(libs.ktor.serialization.kotlinx.json)
            // Database
            implementation(libs.sqlde.light.runtime)
            // DI
            implementation(libs.koin.core)

            // Utils
            implementation("com.soywiz.korlibs.klock:klock:4.0.5")
            implementation("io.ktor:ktor-client-logging:2.3.6")
            // DateTime
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
        }
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.identity.jvm)
            implementation(libs.transport.runtime)
            implementation(libs.androidx.runtime.android)
            implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
            // ViewModel
            //implementation("com.rickclephas.kmm:kmm-viewmodel-core:1.0.0")
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
    }
}

sqldelight {
    databases {
        create("ChatDatabase") {
            packageName.set("com.shuli.cc.app.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/schemas"))
        }
    }
}
android {
    namespace = "com.shuli.cc.app"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
