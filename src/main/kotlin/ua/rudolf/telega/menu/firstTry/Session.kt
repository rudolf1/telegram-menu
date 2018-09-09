package ua.rudolf.telega.menu.firstTry

import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import ua.rudolf.telega.menu.DefaultMessageSentCallback
import ua.rudolf.telega.menu.SetPropertyWaitingReplyAction
import ua.rudolf.telega.menu.WaitingReplyAction
import ua.rudolf.telega.menu.extractMessage

class Session<T>(val userSession: T, val menu: Menu<T>) {

    val awaitingReply = HashMap<Message, WaitingReplyAction>()

    fun process(update: Update): List<BotApiMethod<Message>> {
        val message = update.extractMessage()
        val result = ArrayList<BotApiMethod<Message>>()

        when {
            message.location != null -> {
                menu.locationHandlers.forEach { it.invoke(userSession, message.location, result) }
            }
            message.isReply -> {
                awaitingReply
                        .filter { it.key.messageId == message.replyToMessage.messageId }
                        .forEach { it.value.invoke(update, result) }
            }
            message.text != null -> {
                menu.properties
                        .filter { message.text.startsWith(it.key) }
                        .entries.firstOrNull()
                        ?.also {
                            val property = it.value.invoke(userSession)
                            menu.bot.executeAsync(SendMessage(message.chatId, "Please enter new value for ${it.key} (old value ${property.get()})"),
                                    DefaultMessageSentCallback<Message>(handleResult = { _, message ->
                                        println("Execute $message")
                                        awaitingReply.put(message, SetPropertyWaitingReplyAction(it.key, property, menu.bot))
                                    }))
                        }
                menu.config.get(message.text)?.also {
                    it.invoke(userSession, result)
                }

            }
        }
        if (result.isEmpty()) {
            menu.config.get(null)?.invoke(userSession, result)
        }
        return result
    }

}
