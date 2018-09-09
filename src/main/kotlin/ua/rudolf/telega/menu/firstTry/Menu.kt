package ua.rudolf.telega.menu.firstTry

import org.telegram.telegrambots.api.methods.BotApiMethod
import org.telegram.telegrambots.api.objects.Location
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.AbsSender
import ua.rudolf.telega.menu.MutableProperty
import ua.rudolf.telega.menu.extractMessage
import kotlin.reflect.KMutableProperty0


class Menu<T>(val bot: AbsSender, val userSessionProvider: (update: Update) -> T) {

    val sessions = HashMap<Long, Session<T>>()

    val config = HashMap<String?, (user: T, result: MutableList<BotApiMethod<Message>>) -> Unit>()
    val properties = HashMap<String, (user: T) -> MutableProperty<in String>>()
    val locationHandlers = HashSet<(user: T, location: Location, result: MutableList<BotApiMethod<Message>>) -> Unit>()

    fun add(text: String? = null, f: (user: T, result: MutableList<BotApiMethod<Message>>) -> Unit) = config.put(text, f)

    fun addParamKotlin(text: String, param: (user: T) -> KMutableProperty0<in String>) {
        properties.put(text, { user -> MutableProperty.create(param.invoke(user)) })
    }

    fun addParam(text: String, param: (user: T) -> MutableProperty<in String>) {
        properties.put(text, param::invoke)
    }


    fun process(update: Update): List<BotApiMethod<Message>> {
        val message = update.extractMessage()
        val session = sessions.getOrPut(message.chatId, { Session(userSessionProvider.invoke(update), this) })
        return session.process(update)
    }

    fun locationHandler(function: (user: T, location: Location, result: MutableList<BotApiMethod<Message>>) -> Unit) {
        locationHandlers.add(function)

    }


}