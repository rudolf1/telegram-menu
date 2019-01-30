package ua.rudolf.telega.menu.bottom

import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.api.methods.PartialBotApiMethod
import org.telegram.telegrambots.api.methods.send.SendDocument
import org.telegram.telegrambots.api.methods.send.SendLocation
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.bots.AbsSender
import org.telegram.telegrambots.updateshandlers.SentCallback
import ua.rudolf.telega.menu.DefaultMessageSentCallback
import java.io.ByteArrayInputStream

fun <T> Actionable<T>.locationMessage(longitude: Float, latitude: Float, callback: SentCallback<Message> = DefaultMessageSentCallback()) {
    this.act(ApiTelegramCommandAndCallback(SendLocation().apply {
        this.chatId = this@locationMessage.session.chatId.toString()
        this.longitude = longitude
        this.latitude = latitude
    }, { b, c -> executeAsync(b, c) }, callback))
}

fun <T> Actionable<T>.textMessage(text: String, callback: SentCallback<Message> = DefaultMessageSentCallback()) {
    this.act(ApiTelegramCommandAndCallback(SendMessage(this.session.chatId, text), { b, c -> executeAsync(b, c) }, callback))
}

fun <T> Actionable<T>.keyboard(keyBoard: ReplyKeyboardMarkup, callback: SentCallback<Message> = DefaultMessageSentCallback()) {
    keyboardInternal(this.session.chatId, keyBoard, callback).forEach { this.act(it) }
}

internal fun keyboardInternal(chatId: Long, keyBoard: ReplyKeyboardMarkup, callback: SentCallback<Message> = DefaultMessageSentCallback()): List<TelegramCommand> {
    return listOf(ApiTelegramCommandAndCallback(SendMessage().also {
        it.text = "None"
        it.chatId = chatId.toString()
        it.replyMarkup = keyBoard
    }, { b, c -> executeAsync(b, c) }, callback))
}

fun <T> Actionable<T>.documentMessage(documentName: String, ba: ByteArray) {
    this.act(PartialApiTelegramCommandAndCallback<SendDocument>(SendDocument().apply {
        this.chatId = this@documentMessage.session.chatId.toString()
        this.setNewDocument(documentName, ByteArrayInputStream(ba))
    }) { b -> sendDocument(b) })
}

interface TelegramCommand {
    fun execute(sender: AbsSender)
}

class ApiTelegramCommandAndCallback(
        val method: BotApiMethod<Message>,
        val run: AbsSender.(BotApiMethod<Message>, SentCallback<Message>) -> Unit,
        val callback: SentCallback<Message> = DefaultMessageSentCallback()
) : TelegramCommand {

    override fun execute(sender: AbsSender) {
        run.invoke(sender, method, callback)
    }
}

class PartialApiTelegramCommandAndCallback<T : PartialBotApiMethod<Message>>(
        val method: T,
        val run: AbsSender.(T) -> Unit
) : TelegramCommand {
    override fun execute(sender: AbsSender) {
        sender.run(method)
    }
}
