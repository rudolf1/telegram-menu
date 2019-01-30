package ua.rudolf.sample.dao

enum class UserState {
    NEW,
    READY,
    TERMINATED
}

class UserSession(val chatId: Long) {
    var lastLocation: Location? = null
    var state: UserState = UserState.NEW
}