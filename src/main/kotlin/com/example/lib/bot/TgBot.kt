package com.example.lib.bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.network.fold
import org.slf4j.LoggerFactory

class TgBot(
    private val botToken: String,
    private val iBotCases: List<IBotCase>
) : IBot {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val askReplyWorker = AskReplyWorker()

    override fun getBot() = bot

    override fun getAskReplyWorker() = askReplyWorker

    private var bot: Bot = bot {
        token = botToken
        timeout = 30
        dispatch {
            message {
                if (askReplyWorker.proceedIfCan(update)) {
                    logger.info("Proceeded by AskReplyWorker")
                    return@message
                }
            }
            iBotCases.forEach {
                this.addHandler(it.getHandler(this@TgBot))
            }
        }
    }

    init {
        logger.info("Start pooling. Token: $botToken")
        bot.startPolling()
    }

    override fun getFileUrl(fileId: String, onResult: (url: String?, fileSize: Int?) -> Unit) {
        getBot().getFile(fileId).fold({
            val filePath = it?.result?.filePath
            if (filePath != null) {
                logger.info("File path: $filePath")
                onResult("https://api.telegram.org/file/bot${botToken}/$filePath", it.result?.fileSize)
            } else {
                logger.error("Failed to get file path")
                onResult(null, null)
            }
        })
    }

}