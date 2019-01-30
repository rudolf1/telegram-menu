package ua.rudolf.sample

import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import ua.rudolf.sample.dao.Location
import ua.rudolf.sample.dao.UserSession
import ua.rudolf.sample.dao.UserState
import ua.rudolf.telega.menu.TelegramCommand
import ua.rudolf.telega.menu.bottom.Menu
import ua.rudolf.telega.menu.documentMessage
import ua.rudolf.telega.menu.extractMessage
import ua.rudolf.telega.menu.locationMessage
import ua.rudolf.telega.menu.textMessage

class TelegramBot(config: TelegramBotConfig) {

    val bot = PollingBot(
            config = config,
            botUsernameParam = "SampleBot",
            onUpdate = this::onUpdateReceived
    )

    private val bottomMenu = Menu(
            bot = bot,
            userSessionProvider = { UserSession(it.extractMessage().chatId) },
            menuGenerator = { user, menuPoint ->
                menuPoint
                        .menu("Change state ${user.chatId}") { _, menu ->
                            UserState.values().forEach {
                                menu.action(it.name) { user ->
                                    user.state = it
                                    textMessage(user.chatId, "Class $it")
                                }
                            }
                        }
                        .action("Show last location") { menuUser ->
                            val loc = menuUser.lastLocation
                            if (loc != null) {
                                locationMessage(menuUser.chatId, loc.longitude, loc.latitude)
                            } else {
                                textMessage(menuUser.chatId, "Last location not defined").plus(
                                    documentMessage(menuUser.chatId, "read_it.txt", "Hi Looser".toByteArray())
                                )

                            }
                        }
            }
    ).apply {
        this.locationHandler(::appLocationHandler)
    }

    private fun appLocationHandler(user: UserSession, message: Message, location: org.telegram.telegrambots.api.objects.Location, r: MutableList<TelegramCommand>) {
        if (user.state == UserState.TERMINATED) {
            r.addAll(textMessage(user.chatId.toString(), "User terminated"))
            return
        }
        user.lastLocation = Location.new(message, location)

        r.addAll(locationMessage(user.chatId, location.longitude, location.latitude))
    }


    private fun onUpdateReceived(update: Update) {
//        databaseTransaction {
        bottomMenu.process(update).forEach {
            it.execute(bot)
        }
//        }
    }
}
