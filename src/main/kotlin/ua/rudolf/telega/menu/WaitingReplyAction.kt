package ua.rudolf.telega.menu

import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update

interface WaitingReplyAction {
    fun invoke(update: Update, result: MutableList<BotApiMethod<Message>>)
}

