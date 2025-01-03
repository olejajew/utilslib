import com.libs.data.DatabaseInstance
import com.libs.data.success
import com.libs.generateId
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.javatime.JavaInstantColumnType
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class NpsRequestTable(private val databaseInstance: DatabaseInstance) : IdTable<String>("nps_request_v1") {

    val requestId = varchar("id", 32).entityId().uniqueIndex()
    override val id: Column<EntityID<String>>
        get() = requestId
    val chatInstanceId = varchar("chat_instance_id", 64)
    val chatId = long("chat_id")
    val requestDate = registerColumn<Instant>("request_date", JavaInstantColumnType())
    val questionGroup = integer("question_group")

    fun saveRequest(instanceId: String, mChatId: Long, questionGroup: Int): String? {
        return databaseInstance.transaction {
            it.success(this.insertAndGetId {
                it[requestId] = generateId(12)
                it[chatInstanceId] = instanceId
                it[chatId] = mChatId
                it[this@NpsRequestTable.questionGroup] = questionGroup
                it[requestDate] = Instant.now()
            }.value)
        }.getData()
    }

}