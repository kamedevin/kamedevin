pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "kamedevin-budget"

include(
    ":app",
    ":core:model",
    ":core:database",
    ":core:data",
    ":core:domain",
    ":core:designsystem",
    ":feature:home",
    ":feature:entry",
    ":feature:history",
    ":feature:categories",
    ":feature:accounts",
    ":feature:settings",
    ":widget",
    ":backup:api",
    ":backup:googledrive",
)
