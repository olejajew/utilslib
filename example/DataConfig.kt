package example

import com.example.buscore.services.amo.AmoUsersTable
import com.example.buscore.data.tables.ConfigTable
import com.example.buscore.data.tables.EntitiesTable
import com.example.buscore.services.reseiver.data.ReceivedLeadsTable
import com.example.buscore.services.sheets.data.SheetsTable
import com.example.buscore.utm.data.ExpressionTable
import com.libs.data.DatabaseRepository
import org.jetbrains.exposed.sql.Table
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataConfig(
    @Value("\${spring.datasource.url}")
    private var url: String,
    @Value("\${spring.datasource.driver:org.postgresql.Driver}")
    private var driver: String,
    @Value("\${spring.datasource.username}")
    private var username: String,
    @Value("\${spring.datasource.password}")
    private var password: String,
) {

    private val tables = arrayOf<Table>(
        ConfigTable,
        EntitiesTable,
        AmoUsersTable,
        SheetsTable,
        ReceivedLeadsTable,
        ExpressionTable
    )

    @Bean
    fun getDatabaseRepository(): DatabaseRepository {
        return DatabaseRepository(url, driver, username, password, tables)
    }

}