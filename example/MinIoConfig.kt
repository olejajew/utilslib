package example

import com.libs.files.MinIoService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinIoConfig(
    @Value("\${api.storage.url:}")
    private var storagePath: String,
    @Value("\${api.storage.username:}")
    private var storageUsername: String,
    @Value("\${api.storage.password:}")
    private var storagePassword: String,
    @Value("\${api.storage.bucket:}")
    private var storageBucket: String,
) {

    @Bean
    fun getMinIoService(): MinIoService {
        return MinIoService(storagePath, storageUsername, storagePassword, storageBucket)
    }

}