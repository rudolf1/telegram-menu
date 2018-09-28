package ua.rudolf.telega.menu

import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.api.methods.PartialBotApiMethod
import org.telegram.telegrambots.api.methods.send.SendDocument
import org.telegram.telegrambots.api.methods.send.SendLocation
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.bots.AbsSender
import org.telegram.telegrambots.updateshandlers.SentCallback
import java.io.ByteArrayInputStream

fun none() = listOf<TelegramCommand>()

fun locationMessage(chatId: Long, longitude: Float, latitude: Float, callback: SentCallback<Message> = DefaultMessageSentCallback()): List<TelegramCommand> {
    return listOf(ApiTelegramCommandAndCallback(SendLocation().apply {
        this.chatId = chatId.toString()
        this.longitude = longitude
        this.latitude = latitude
    }, { b, c -> executeAsync(b, c) }, callback))
}

fun textMessage(chatId: Long, text: String, callback: SentCallback<Message> = DefaultMessageSentCallback()) = textMessage(chatId.toString(), text, callback)

fun textMessage(chatId: String, text: String, callback: SentCallback<Message> = DefaultMessageSentCallback()): List<TelegramCommand> {
    return listOf(ApiTelegramCommandAndCallback(SendMessage(chatId, text), { b, c -> executeAsync(b, c) }, callback))
}

fun keyboard(chatId: Long, keyBoard: ReplyKeyboardMarkup, callback: SentCallback<Message> = DefaultMessageSentCallback()): List<TelegramCommand> {
    return listOf(ApiTelegramCommandAndCallback(SendMessage().also {
        it.text = "None"
        it.chatId = chatId.toString()
        it.replyMarkup = keyBoard
    }, { b, c -> executeAsync(b, c) }, callback))
}

fun documentMessage(chatId: String, documentName: String, ba: ByteArray): List<TelegramCommand> {
    return listOf(PartialApiTelegramCommandAndCallback<SendDocument>(SendDocument().apply {
        this.chatId = chatId
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
