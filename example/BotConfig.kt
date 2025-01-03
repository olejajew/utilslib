package example

import com.libs.bot.IBotCase
import com.libs.bot.TgBot
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class BotConfig(
    @Value("\${bot.token}")
    val token: String,
    private val iBotCases: List<IBotCase>
) {

    private var tgBot: TgBot? = null

    @Bean
    fun getTgBot(): TgBot {
        if (tgBot == null) {
            tgBot = TgBot(token, iBotCases)
        }
        return tgBot!!
    }
}