pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://maven.aliyun.com/repository/public")

    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven(url = "https://maven.aliyun.com/repository/public")
        maven { setUrl("https://android-sdk.is.com") }
        maven { setUrl("https://jitpack.io") }

    }
}

rootProject.name = "DynamicIslandVip"
include(":app")
include(":color_picker")

 