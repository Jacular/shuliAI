import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelightPlugin)
    alias(libs.plugins.ktorPlugin)
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
            implementation(libs.compose.ui.tooling)
            implementation(libs.compose.material3)
            implementation(libs.compose.foundation)
            implementation(libs.androidx.runtime.android)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            // Serialization
            implementation(libs.ktor.serialization.kotlinx.json)
            // Database
            implementation(libs.sqlde.light.runtime)
            implementation(libs.koin.core)
            // DI
            implementation("io.insert-koin:koin-core:3.5.3")

            // ViewModel
            implementation("com.rickclephas.kmm:kmm-viewmodel-core:1.0.0")

            // Utils
            implementation("com.soywiz.korlibs.klock:klock:4.0.5")
            implementation("io.ktor:ktor-client-logging:2.3.6")
            // DateTime
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
            implementation("io.insert-koin:koin-android:3.5.3")
            implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
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
            packageName.set("com.shuli.cc.database")
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
dependencies {
    implementation(libs.identity.jvm)
    implementation(libs.transport.runtime)
    implementation(libs.androidx.runtime.android)
    implementation(project(":shared"))
}
