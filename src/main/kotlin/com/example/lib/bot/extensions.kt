package com.example.lib.bot

import com.github.kotlintelegrambot.entities.Update

fun Update?.text() = this?.message?.text

fun Update?.callbackData() = this?.callbackQuery?.data

fun Update.chatId() = this.message?.chat?.id ?: this.callbackQuery?.message?.chat?.id

fun Update.userId() = this.message?.from?.id ?: this.callbackQuery?.from?.id

fun Update.username() = (this.message?.from?.firstName?.let { "$it " } ?: "") +
        (this.message?.from?.lastName?.let { "$it " } ?: "") +
        (this.message?.from?.username?.let { "($it)" } ?: "")
