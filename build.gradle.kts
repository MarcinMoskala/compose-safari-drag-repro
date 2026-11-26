plugins {
    kotlin("multiplatform") version "2.4.21"
    kotlin("plugin.compose") version "2.4.21"
    id("org.jetbrains.compose") version "1.12.2"
}

kotlin {
    wasmJs {
        outputModuleName.set("repro")
        browser {
            commonWebpackConfig { outputFileName = "repro.js" }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:1.12.2")
            implementation("org.jetbrains.compose.foundation:foundation:1.12.2")
            implementation("org.jetbrains.compose.ui:ui:1.12.2")
            implementation("org.jetbrains.compose.material3:material3:1.12.0-alpha03")
        }
    }
}

// SKIKO_FORWARDER_FIX=1 turns on the workaround in webpack.config.d/safariSkikoGlue.js: rebuild when it changes.
tasks.matching { it.name.startsWith("wasmJsBrowser") && it.name.endsWith("Webpack") }.configureEach {
    inputs.property("skikoForwarderFix", providers.environmentVariable("SKIKO_FORWARDER_FIX").orElse("0"))
}
