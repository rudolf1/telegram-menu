package ua.rudolf.sample

import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi

val config = TelegramBotConfig(
        telegramBotToken = "BOT_TOKEN",
        telegramProxy = TelegramBotProxyConfig(
                hostname = "",
                port = 0,
                username = "",
                password = ""
        )
)

fun main(args: Array<String>) {
    ApiContextInitializer.init()
    val telegramBotsApi = TelegramBotsApi()

    val telegramBot = TelegramBot(config)
    telegramBotsApi.registerBot(telegramBot.bot)
    while (true) {
        Thread.sleep(1000)
    }

}
