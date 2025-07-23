package com.edu.achadosufc.ui.screen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash_screen")

    data object Home : Screen("home/{userId}") {
        fun createRoute(userId: Int): String = "home/$userId"
    }

    data object ReportItem : Screen("report_item")
    data object Login : Screen("login")
    data object Profile : Screen("profile")

    data object ItemDetail : Screen("itemDetail/{itemId}") {
        fun createRoute(itemId: Int) = "itemDetail/$itemId"
    }

    data object SignUp : Screen("sign_up_screen")

    data object Search : Screen("search_screen")

    data object UserDetail : Screen("user_detail/{userId}") {
        fun createRoute(userId: Int) = "user_detail/$userId"
    }

    data object Chat :
        Screen("chat/{recipientId}/{recipientUsername}/{itemName}/{itemId}?recipientPhotoUrl={recipientPhotoUrl}&itemPhotoUrl={itemPhotoUrl}") {
        fun createRoute(
            recipientId: Int,
            recipientUsername: String,
            itemName: String,
            itemId: Int,
            recipientPhotoUrl: String?,
            itemPhotoUrl: String?
        ) =
            "chat/$recipientId/$recipientUsername/$itemName/$itemId?recipientPhotoUrl=${java.net.URLEncoder.encode(recipientPhotoUrl ?: "", "UTF-8")}&itemPhotoUrl=${java.net.URLEncoder.encode(itemPhotoUrl ?: "", "UTF-8")}"
    }

}

