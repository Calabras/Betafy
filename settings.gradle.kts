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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BetafyApp"
include(":app")
include(":feature:auth")
include(":feature:profile")
include(":feature:search")
include(":feature:chats")
include(":feature")
include(":core")
include(":feature:match")

include(":feature:match:di")
include(":feature:match:domain")
include(":feature:match:presentation")
include(":feature:match:data")
include(":feature:chats:di")
include(":feature:chats:domain")
include(":feature:chats:presentation")
include(":feature:chats:data")
include(":feature:profile:di")
include(":feature:profile:domain")
include(":feature:profile:presentation")
include(":feature:profile:data")
include(":feature:auth:di")
include(":feature:auth:domain")
include(":feature:auth:presentation")
include(":feature:auth:data")
include(":feature:search:di")
include(":feature:search:domain")
include(":feature:search:presentation")
include(":feature:search:data")
include(":core:network")
include(":core:ui-kit")
include(":core:database")
include(":core:common")
include(":app:presentation")
include(":feature:splash")
include(":feature:splash:presentation")
