pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // jcenter() is deprecated; consider removing it or using an alternative
        maven(url = uri("https://jitpack.io"))
        maven (
            url = "https://phonepe.mycloudrepo.io/public/repositories/phonepe-intentsdk-android"
        )

    }
}

rootProject.name = "QuickCart"
include(":app")
