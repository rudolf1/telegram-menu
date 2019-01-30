package ua.rudolf.telega.menu.bottom

import org.telegram.telegrambots.api.objects.Location
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.AbsSender
import ua.rudolf.telega.menu.DefaultMessageSentCallback
import ua.rudolf.telega.menu.extractMessage

fun <T> MenuPoint<T>.sendToUser(user: T, chatId: Long, f: (MenuPoint<T>) -> Unit, editMessage: Int? = null): List<TelegramCommand> {
    return keyboardInternal(
            chatId,
            this.inlineKeyboardMarkup(user),
            DefaultMessageSentCallback(handleResult = { _, message ->
                println("Execute $message")
                f(this@sendToUser)
            })
    )
}

class Menu<T>(
        val bot: AbsSender,
        val userSessionProvider: (update: Update) -> T,
        val locationHandler: Actionable<T>.(message: Message, location: Location) -> Unit = { message: Message, location: Location -> },
        menuGenerator: Generator<T>
) {

    val bottomKeyboard = MenuPoint<T>(null, id = "", generator = menuGenerator)
    val sessions = HashMap<Long, Session<T>>()

    fun process(update: Update): List<TelegramCommand> {
        val message = update.extractMessage()
        val session = sessions.getOrPut(message.chatId, { Session(userSessionProvider.invoke(update), this) })
        if (session.currentMenu == null) {
            return bottomKeyboard.sendToUser(session.userSession, message.chatId, { v -> session.currentMenu = v })
        }
        return session.process(update)
    }
}