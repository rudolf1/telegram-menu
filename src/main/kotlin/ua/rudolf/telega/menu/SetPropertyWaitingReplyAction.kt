package ua.rudolf.telega.menu

import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.AbsSender

class SetPropertyWaitingReplyAction(val propertyName: String, val property: MutableProperty<in String>, val bot: AbsSender) : WaitingReplyAction {
    override fun invoke(update: Update, result: MutableList<BotApiMethod<Message>>) {
        val message = update.extractMessage()
        property.set(message.text)
        result.add(SendMessage(message.chatId, "Value ${propertyName} set to ${property.get()}"))
    }
}