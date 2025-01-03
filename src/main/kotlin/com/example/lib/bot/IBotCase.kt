package com.example.lib.bot

import com.example.lib.bot.IBot
import com.github.kotlintelegrambot.dispatcher.Dispatcher
import com.github.kotlintelegrambot.dispatcher.handlers.Handler

interface IBotCase {

    /**
     * dispatcher.addHandler(object : Handler {//...})
     */
    fun getHandler(iBot: IBot): Handler

}