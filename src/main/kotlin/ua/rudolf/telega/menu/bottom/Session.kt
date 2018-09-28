package ua.rudolf.telega.menu.bottom

import org.telegram.telegrambots.api.objects.Update
import ua.rudolf.telega.extractMessage
import ua.rudolf.telega.menu.TelegramCommand
import ua.rudolf.telega.menu.textMessage

class Session<T>(val userSession: T, val menu: Menu<T>) {

    var currentMenu: MenuPoint<T>? = null
    var awaitingTextReply: String? = null


    fun process(update: Update): List<TelegramCommand> {
        val message = update.extractMessage()
        val result = ArrayList<TelegramCommand>()

        when {
            message.location != null -> {
                menu.locationHandlers.forEach { it.invoke(userSession, message, message.location, result) }
            }
            message.hasText() -> {
                val incomingText = message.text
                val menuContent = (currentMenu ?: menu.bottomKeyboard).createContent(userSession)
                menuContent.menus.get(incomingText)?.apply {
                    result.addAll(this.sendToUser(userSession, message.chatId, { v -> currentMenu = v }, editMessage = message.messageId))
                }

                menuContent.actions.get(incomingText)?.apply {
                    result.addAll(this.invoke(userSession))
                    val parent = currentMenu?.parent
                    if (parent != null) {
                        result.addAll(parent.sendToUser(userSession, message.chatId, { v -> currentMenu = v }, editMessage = message.messageId))
                    }
                }

                menuContent.properties.get(incomingText)?.apply {
                    //                    this.sendToUser(menu.bot, message.chatId, {v -> currentMenu = v } )
                }

            }
        }
        if (result.isEmpty()) {
            result.addAll(textMessage(message.chatId, "Incorrect message, Please use menu."))
        }
        return result
    }

}
