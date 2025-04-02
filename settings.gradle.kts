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
        maven (
            "https://artifactory-external.vkpartner.ru/artifactory/vkid-sdk-android/"
        )
    }
}

<<<<<<< HEAD
rootProject.name = "Maps&Friends"
=======
rootProject.name = "vkid"
>>>>>>> ffa6dfdfb23959ad72d9b30a9c1f6f1099a32671
include(":app")
 