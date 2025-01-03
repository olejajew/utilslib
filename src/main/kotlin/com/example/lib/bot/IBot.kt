package com.example.lib.bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.Update

interface IBot {

    fun getAskReplyWorker(): AskReplyWorker

    fun getBot(): Bot

    fun getFileUrl(fileId: String, onResult: (url: String?, fileSize: Int?) -> Unit)

    fun requestReply(chatId: Long, text: String, onResult: (Update?) -> Unit) {
        val messageId = getBot().sendMessage(
            ChatId.fromId(chatId), text, parseMode = ParseMode.HTML
        ).let {
            if (it.isError) {
                return onResult(null)
            } else {
                it.get().messageId
            }
        }
        getAskReplyWorker().saveRequest(chatId, messageId) {
            onResult(it)
        }
    }
}