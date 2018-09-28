package ua.rudolf.telega.menu.bottom

import org.telegram.telegrambots.api.objects.Location
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.AbsSender
import ua.rudolf.telega.menu.DefaultMessageSentCallback
import ua.rudolf.telega.menu.TelegramCommand
import ua.rudolf.telega.menu.extractMessage
import ua.rudolf.telega.menu.keyboard

fun <T> MenuPoint<T>.sendToUser(user: T, chatId: Long, f: (MenuPoint<T>) -> Unit, editMessage: Int? = null): List<TelegramCommand> {
    return keyboard(
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
        menuGenerator: Generator<T>
) {

    val bottomKeyboard = MenuPoint<T>(null, id = "", generator = menuGenerator)
    val sessions = HashMap<Long, Session<T>>()

    val locationHandlers = HashSet<(user: T, message: Message, location: Location, result: MutableList<TelegramCommand>) -> Unit>()

    fun process(update: Update): List<TelegramCommand> {
        val message = update.extractMessage()
        val session = sessions.getOrPut(message.chatId, { Session(userSessionProvider.invoke(update), this) })
        if (session.currentMenu == null) {
            return bottomKeyboard.sendToUser(session.userSession, message.chatId, { v -> session.currentMenu = v })
        }
        return session.process(update)
    }

    fun locationHandler(function: (user: T, message: Message, location: Location, result: MutableList<TelegramCommand>) -> Unit) {
        locationHandlers.add(function)
    }


}