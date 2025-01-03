package com.example.lib.bot

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Update

class AskReplyWorker(private val count: Int = 20) {

    private val container = mutableMapOf<String, ReplyMessageRequest>()

    fun proceedIfCan(update: Update): Boolean {
        val chatId = ChatId.fromId(update.message!!.chat.id).id
        val replyMessageId = update.message?.replyToMessage?.messageId ?: return false
        val id = generateId(chatId, replyMessageId)
        val action = container[id] ?: return false
        action.onResult(update)
        container.remove(generateId(chatId, replyMessageId))
        return true
    }


    private fun generateId(chatId: Long, replyMessageId: Long): String {
        return "$chatId:$replyMessageId"
    }

    fun saveRequest(chatId: Long, replyMessageId: Long, onResult: (Update) -> Unit) {
        if (container.size > count) {
            val oldest = container.values.minByOrNull { it.addedDate }
            if (oldest != null) {
                container.remove(generateId(oldest.chatId, oldest.replyMessageId))
            }
        }
        val replyMessageRequest = ReplyMessageRequest(System.currentTimeMillis(), chatId, replyMessageId, onResult)
        container[generateId(chatId, replyMessageId)] = replyMessageRequest
    }

    internal data class ReplyMessageRequest(
        val addedDate: Long,
        val chatId: Long,
        val replyMessageId: Long,
        val onResult: (Update) -> Unit
    )
}