package com.betafy.presentation.navigation.model

enum class AccentRole { Lavender, Tangerine, Mint, Blush }

data class HeaderPart(
    val text: String,
    val accent: AccentRole? = null
)

sealed class NavigationBottomModel(
    val route: String,
    val text: String,
    val accent: AccentRole,
    val caption: String,
    val headerLines: List<List<HeaderPart>>,
    val iconPaths: List<String>
) {

    data object Search : NavigationBottomModel(
        route = "search",
        text = "Поиск",
        accent = AccentRole.Lavender,
        caption = "Добрый день",
        headerLines = listOf(
            listOf(HeaderPart("Привет, "), HeaderPart("Анна", AccentRole.Lavender))
        ),
        iconPaths = listOf(
            "M3.5 10.5 a7 7 0 1 0 14 0 a7 7 0 1 0 -14 0",
            "M15.5 15.5 L21 21"
        )
    )

    data object Match : NavigationBottomModel(
        route = "match",
        text = "Мэтч",
        accent = AccentRole.Tangerine,
        caption = "Сегодня для тебя",
        headerLines = listOf(
            listOf(HeaderPart("Твои "), HeaderPart("мэтчи", AccentRole.Tangerine))
        ),
        iconPaths = listOf(
            "M12 20.5 C5 15.5 3 12 3 8.8 A4.8 4.8 0 0 1 12 6.6 A4.8 4.8 0 0 1 21 8.8 C21 12 19 15.5 12 20.5 Z"
        )
    )

    data object Chats : NavigationBottomModel(
        route = "chats",
        text = "Чаты",
        accent = AccentRole.Mint,
        caption = "Общение",
        headerLines = listOf(
            listOf(HeaderPart("Твои "), HeaderPart("чаты", AccentRole.Mint))
        ),
        iconPaths = listOf(
            "M4 5 H20 A1 1 0 0 1 21 6 V16 A1 1 0 0 1 20 17 H10 L5 21 V17 H4 A1 1 0 0 1 3 16 V6 A1 1 0 0 1 4 5 Z"
        )
    )

    data object Profile : NavigationBottomModel(
        route = "profile",
        text = "Профиль",
        accent = AccentRole.Blush,
        caption = "Аккаунт",
        headerLines = listOf(
            listOf(HeaderPart("Мой "), HeaderPart("профиль", AccentRole.Blush))
        ),
        iconPaths = listOf(
            "M8 8 a4 4 0 1 0 8 0 a4 4 0 1 0 -8 0",
            "M4.5 20.5 C4.5 16.5 8 14.5 12 14.5 C16 14.5 19.5 16.5 19.5 20.5"
        )
    )

    companion object {
        fun getNavItems(): List<NavigationBottomModel> {
            return listOf(
                Search,
                Match,
                Chats,
                Profile
            )
        }
    }
}