package com.example.lib.bot

import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Update

class AskReplyWorker(private val count: Int = 20) {

    private val container = mutableMapOf<String, ReplyMessageRequest>()
    private val listenAll = mutableMapOf<Long, ListenAllRequest>()

    fun proceedIfCan(update: Update): Boolean {
        val chatId = update.chatId() ?: return false
        listenAll[chatId]?.let {
            it.onResult(update)
            return true
        }

        val replyMessageId =
            update.message?.replyToMessage?.messageId ?: update.callbackQuery?.message?.messageId ?: return false
        val id = generateId(chatId, replyMessageId)
        val action = container[id] ?: return false
        action.onResult(update)
        return true
    }


    private fun generateId(chatId: Long, replyMessageId: Long): String {
        return "$chatId:$replyMessageId"
    }

    /**
     * @param onResult - return true if you want to delete this request after result
     */
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

    fun listenAll(chatId: Long, onResult: (Update) -> Unit){
        if (listenAll.size > count) {
            val oldest = listenAll.values.minByOrNull { it.addedDate }
            if (oldest != null) {
                listenAll.remove(oldest.chatId)
            }
        }
        val request = ListenAllRequest(System.currentTimeMillis(), chatId, onResult)
        listenAll[chatId] = request
    }

    internal data class ReplyMessageRequest(
        val addedDate: Long,
        val chatId: Long,
        val replyMessageId: Long,
        val onResult: (Update) -> Unit
    )

    internal data class ListenAllRequest(
        val addedDate: Long,
        val chatId: Long,
        val onResult: (Update) -> Unit
    )
}