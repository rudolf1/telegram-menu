package ua.rudolf.telega.menu

import org.apache.http.HttpHost
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import kotlin.reflect.KFunction1

data class TelegramBotConfig(
        val telegramBotToken: String,
        val telegramProxy: TelegramBotProxyConfig? = null
)

data class TelegramBotProxyConfig(
        val hostname: String,
        val port: Int,
        val username: String,
        val password: String

)

class PollingBot(
        config: TelegramBotConfig,
        val botUsernameParam: String,
        val onUpdate: KFunction1<@ParameterName(name = "update") Update, Unit>
) : TelegramLongPollingBot(DefaultBotOptions().apply {
    config.telegramProxy?.also {
        this.httpProxy = HttpHost(it.hostname, it.port)
        this.credentialsProvider = BasicCredentialsProvider().also { crProvider ->
            crProvider.setCredentials(org.apache.http.auth.AuthScope.ANY, UsernamePasswordCredentials(it.username, it.password))
        }
    }
}) {

    private val botToken: String = config.telegramBotToken

    override fun getBotToken(): String = botToken

    override fun getBotUsername(): String = botUsernameParam

    override fun onUpdateReceived(update: Update) = onUpdate(update)

}