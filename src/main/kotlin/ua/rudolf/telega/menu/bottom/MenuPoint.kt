package ua.rudolf.telega.menu.bottom

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow
import ua.rudolf.tracking.menu.TelegramCommand
import ua.rudolf.tracking.menu.MutableProperty
import kotlin.reflect.KMutableProperty0

typealias Generator<T> = (user: T, menuPoint: TmpMenu<T>) -> Unit

class TmpMenu<T>(val origin: MenuPoint<T>, user: T) {
    val properties = HashMap<String, (user: T) -> MutableProperty<in String>>()
    val actions = HashMap<String?, (user: T) -> List<TelegramCommand>>()
    val menus = LinkedHashMap<String, MenuPoint<T>>()

    init {
        origin.generator.invoke(user, this)
        if (origin.parent != null) {
            val text = "Back"
            menus.put(mapKey(text), origin.parent)
        }
    }

    private fun hash(text: String): String = "${origin.id}->$text"

    //    private fun mapKey(text: String): String = "${hash(text).invisibleHash()}$text"
    private fun mapKey(text: String): String = text

    fun menu(text: String, f: Generator<T>): TmpMenu<T> {

        menus.put(mapKey(text), MenuPoint(origin, id = hash(text), generator = f))
        return this
    }

    fun addParamKotlin(text: String, param: (user: T) -> KMutableProperty0<in String>): TmpMenu<T> {
        properties.put(mapKey(text), { user -> MutableProperty.create(param.invoke(user)) })
        return this
    }

    fun addParam(text: String, param: (user: T) -> MutableProperty<in String>): TmpMenu<T> {
        properties.put(mapKey(text), param::invoke)
        return this
    }

    fun action(text: String, f: (user: T) -> List<TelegramCommand>): TmpMenu<T> {
        actions.put(mapKey(text), f)
        return this
    }
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

            tmpMenu.menus.keys.plus(tmpMenu.actions.keys).plus(tmpMenu.properties.keys).forEach {
                keyboard.add(KeyboardRow().apply {
                    add(KeyboardButton(it))
                })
            }

        }
    }

}

