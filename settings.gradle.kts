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
        // JAudioTagger and others might require jitpack or custom repos if not in central
        maven { url = java.net.URI("https://artifacts.unidata.ucar.edu/repository/unidata-all/") }
    }
}

rootProject.name = "Melody"
include(":app")
include(":core")
include(":data")
include(":domain")
include(":player")
include(":feature_library")
include(":feature_playlists")
include(":feature_search")
include(":feature_settings")
