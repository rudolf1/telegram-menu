package ua.rudolf.telega.menu

import org.apache.logging.log4j.LogManager
import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.exceptions.TelegramApiRequestException
import org.telegram.telegrambots.updateshandlers.SentCallback
import java.io.Serializable

class DefaultMessageSentCallback<T : Serializable>(
        val handleResult: (botApiMethod: BotApiMethod<T>, message: T) -> Unit = { _, message -> LOG.debug("Message successfully sent: {}", message) },
        val handleError: (botApiMethod: BotApiMethod<T>, e: TelegramApiRequestException) -> Unit = { _, e -> LOG.error("Message failed: {}", e) },
        val handleException: (method: BotApiMethod<T>, exception: Exception) -> Unit = { method, exception -> LOG.error("Method failed: " + method.method, exception) }
) : SentCallback<T> {

    override fun onResult(method: BotApiMethod<T>, response: T) = this.handleResult(method, response)

    override fun onException(method: BotApiMethod<T>, exception: java.lang.Exception) = this.handleException(method, exception)

    override fun onError(method: BotApiMethod<T>, apiException: TelegramApiRequestException) = this.handleError(method, apiException)

    companion object {

        internal val LOG = LogManager.getLogger(DefaultMessageSentCallback::class.java)
    }
}
