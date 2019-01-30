package ua.rudolf.telega.menu.bottom

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow
import ua.rudolf.telega.menu.MutableProperty
import kotlin.reflect.KMutableProperty0

typealias Generator<T> = TmpMenu<T>.() -> Unit
typealias ActionableGenerator<T> = Actionable<T>.() -> Unit

interface Actionable<T> {
    fun act(command: TelegramCommand)
    val user: T
    val session: Session<T>
}

class TmpMenu<T>(val origin: MenuPoint<T>, val user: T) {
    // TODO Make it private
    val properties = HashMap<String, (user: T) -> MutableProperty<in String>>()
    val actions = HashMap<String?, ActionableGenerator<T>>()
    val menus = LinkedHashMap<String, MenuPoint<T>>()

    init {
        this.apply(origin.generator)
        if (origin.parent != null) {
            val text = "Back"
            menus.put(mapKey(text), origin.parent)
        }
    }

    private fun hash(text: String): String = "${origin.id}->$text"

    //    private fun mapKey(text: String): String = "${hash(text).invisibleHash()}$text"
    private fun mapKey(text: String): String = text

    fun menu(text: String, f: Generator<T>) {
        menus.put(mapKey(text), MenuPoint(origin, id = hash(text), generator = f))
    }

    fun addParamKotlin(text: String, param: (user: T) -> KMutableProperty0<in String>) {
        properties.put(mapKey(text), { user -> MutableProperty.create(param.invoke(user)) })
    }

    fun addParam(text: String, param: (user: T) -> MutableProperty<in String>) {
        properties.put(mapKey(text), param::invoke)
    }

    fun action(text: String, f: ActionableGenerator<T>) {
        actions.put(mapKey(text), f)
    }

    fun getButtons() = menus.keys.plus(actions.keys).plus(properties.keys)

}


class MenuPoint<T>(
        val parent: MenuPoint<T>? = null,
        val id: String = "",
        val generator: Generator<T>
) {

    fun createContent(user: T): TmpMenu<T> {
        return TmpMenu<T>(this, user)
    }

    fun inlineKeyboardMarkup(user: T): ReplyKeyboardMarkup {
        val tmpMenu = createContent(user)
        return ReplyKeyboardMarkup().apply {
            this.oneTimeKeyboard = false
            this.selective = true

            tmpMenu.getButtons().forEach {
                keyboard.add(KeyboardRow().apply {
                    add(KeyboardButton(it))
                })
            }

        }
    }

}

