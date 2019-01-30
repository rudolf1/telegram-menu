package ua.rudolf.sample

import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import ua.rudolf.sample.dao.Location
import ua.rudolf.sample.dao.UserSession
import ua.rudolf.sample.dao.UserState
import ua.rudolf.telega.menu.bottom.Actionable
import ua.rudolf.telega.menu.bottom.Menu
import ua.rudolf.telega.menu.bottom.documentMessage
import ua.rudolf.telega.menu.bottom.locationMessage
import ua.rudolf.telega.menu.bottom.textMessage
import ua.rudolf.telega.menu.extractMessage

fun Actionable<UserSession>.appLocationHandler(message: Message, location: org.telegram.telegrambots.api.objects.Location) {
    if (user.state == UserState.TERMINATED) {
        textMessage(user.chatId.toString(), "User terminated")
    } else {
        user.lastLocation = Location.new(message, location)
        locationMessage(user.chatId, location.longitude, location.latitude)
    }
}

class TelegramBot(config: TelegramBotConfig) {

    val bot = PollingBot(
            config = config,
            botUsernameParam = "SampleBot",
            onUpdate = this::onUpdateReceived
    )

    private val bottomMenu = Menu(
            bot = bot,
            userSessionProvider = { UserSession(it.extractMessage().chatId) },
            locationHandler = Actionable<UserSession>::appLocationHandler
    ) {
        menu("Change state ${user.chatId}") {
            UserState.values().forEach {
                action(it.name) {
                    user.state = it
                    textMessage(user.chatId, "User state changed to $it")
                }
            }
        }
        action("Show last location") {
            val loc = user.lastLocation
            if (loc != null) {
                locationMessage(user.chatId, loc.longitude, loc.latitude)
            } else {
                textMessage(user.chatId, "Last location not defined")
                documentMessage(user.chatId, "read_it.txt", "Hi Looser".toByteArray())
            }
        }
    }


    private fun onUpdateReceived(update: Update) {
//        databaseTransaction {
        bottomMenu.process(update).forEach {
            it.execute(bot)
        }
//        }
    }
}
