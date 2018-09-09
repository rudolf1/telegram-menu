package ua.rudolf.telega.menu

import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.updateshandlers.SentCallback
import java.io.Serializable

class ActionWithCallback<T : Serializable>(
        val method: BotApiMethod<T>,
        val callback: SentCallback<T> = DefaultMessageSentCallback()
) {

    @Suppress("UNCHECKED_CAST")
    fun takeMethod(): BotApiMethod<Serializable> = method as BotApiMethod<Serializable>
    @Suppress("UNCHECKED_CAST")
    fun takeCallback(): SentCallback<Serializable> = callback as SentCallback<Serializable>


}