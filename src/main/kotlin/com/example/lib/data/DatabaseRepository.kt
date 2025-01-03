package com.example.lib.data

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.slf4j.LoggerFactory
import java.sql.Connection

class DatabaseRepository(
    url: String,
    driver: String,
    username: String,
    password: String,
    private val tables: Array<Table>
) {
    private var database: Database
    private val logger = LoggerFactory.getLogger(this::class.java)

    init {
        database = Database.connect(
            url = url,
            driver = driver,
            user = username,
            password = password
        )
        TransactionManager.manager.defaultIsolationLevel =
            Connection.TRANSACTION_SERIALIZABLE
        (LoggerFactory.getLogger("Exposed") as? ch.qos.logback.classic.Logger)?.level =
            ch.qos.logback.classic.Level.TRACE

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                *tables
            )
            it.success()
        }
    }

    final fun <T : Any> transaction(
        repeatIfError: Boolean = false,
        function: (Transaction) -> TransactionResult<T>
    ): TransactionResult<T> {
        return org.jetbrains.exposed.sql.transactions.transaction(database) {
            try {
                val result = function.invoke(this)
                if (result is TransactionResult.Error) {
                    logger.error(result.message)
                }
                result
            } catch (e: Exception) {
                if (repeatIfError) {
                    val result = transaction(false, function)
                    result
                } else {
                    logger.error("TRANSACTION ERROR = ${e.localizedMessage}")
                    logger.error(e.stackTraceToString())
                    e.printStackTrace()
                    rollback()
                    TransactionResult.Error(e.localizedMessage ?: "")
                }
            }
        }
    }

}

fun Transaction.disableLogging() {
    (LoggerFactory.getLogger("Exposed") as? ch.qos.logback.classic.Logger)?.level =
        ch.qos.logback.classic.Level.OFF
}

fun Transaction.enableLogging() {
    (LoggerFactory.getLogger("Exposed") as? ch.qos.logback.classic.Logger)?.level =
        ch.qos.logback.classic.Level.TRACE
}

sealed class TransactionResult<T : Any?> {

    data class Success<T : Any?>(val body: T?) : TransactionResult<T>()

    data class Error<T : Any>(val message: String) : TransactionResult<T>()

    fun getData(): T? {
        return if (this is Success) {
            this.body
        } else {
            null
        }
    }

}

/**
 * success -> If request passed without problems
 */
fun <T> Transaction.success(body: T? = null): TransactionResult<T> {
    return TransactionResult.Success(body)
}

/**
 * error -> If request failed
 */
fun <T : Any> Transaction.error(message: String): TransactionResult.Error<T> {
    return TransactionResult.Error(message)
}