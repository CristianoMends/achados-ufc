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

    data object Conversations : Screen("conversations")

    data object Chat :
        Screen("chat_screen/{chatId}/{recipientId}/{recipientName}/{itemId}/{itemTitle}/{itemImageUrl}/{senderId}") {
        fun createRoute(
            chatId: String,
            recipientId: String,
            recipientName: String,
            itemId: String,
            itemTitle: String,
            itemImageUrl: String?,
            senderId: String
        ) =
            "chat_screen/$chatId/$recipientId/$recipientName/$itemId/$itemTitle/${
                java.net.URLEncoder.encode(itemImageUrl ?: "", "UTF-8")}/$senderId"
    }

}

