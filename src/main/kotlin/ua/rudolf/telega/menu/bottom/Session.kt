package ua.rudolf.telega.menu.bottom

import org.telegram.telegrambots.api.objects.Update
import ua.rudolf.telega.menu.extractMessage

class Session<T>(val chatId: Long, val userSession: T, val menu: Menu<T>) {

    var currentMenu: MenuPoint<T>? = null
    var awaitingTextReply: String? = null


    fun process(update: Update): List<TelegramCommand> {
        val message = update.extractMessage()

        val result = ArrayList<TelegramCommand>()
        val x: Actionable<T> = object : Actionable<T> {
            override val session: Session<T>
                get() = this@Session

            override val user: T
                get() = userSession

            override fun act(command: TelegramCommand) {
                result.add(command)
            }
        }

        when {
            message.location != null -> {
                menu.locationHandler(x, message, message.location)
            }
            message.hasText() -> {
                val incomingText = message.text
                val menuContent = (currentMenu ?: menu.bottomKeyboard).createContent(userSession)
                menuContent.menus.get(incomingText)?.apply {
                    result.addAll(this.sendToUser(userSession, message.chatId, { v -> currentMenu = v }, editMessage = message.messageId))
                }

                menuContent.actions.get(incomingText)?.apply {
                    this(x)
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
            with(x) {
                textMessage("Incorrect message, Please use menu.")
            }
        }
        return result
    }

}
