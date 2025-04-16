pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://maven.aliyun.com/repository/public")
        maven(url = "https://jitpack.io")


    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        jcenter()
        maven(url = "https://maven.aliyun.com/repository/public")
        maven { setUrl("https://android-sdk.is.com") }
        maven { setUrl("https://jitpack.io") }

    }
}

rootProject.name = "DynamicIslandVip"
include(":app")
include(":color_picker")

 