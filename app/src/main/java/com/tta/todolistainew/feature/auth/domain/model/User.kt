package com.tta.todolistainew.feature.auth.domain.model

data class User(
    var clientId: Long? = null,
    var serverId: String = "USER_GUEST_SERVER_ID",
    var avatarImage: String = "",
    var googleId: String = "",
    var userName: String = "",
    var lastLogin: String = "",
    var lastUpdate: String = "",
    var email: String = "",
    var hasPayment: Boolean = false,
    // Nếu khác -1 thì sử dụng avatar local
    var avatarLocal : Int = -1,
)
