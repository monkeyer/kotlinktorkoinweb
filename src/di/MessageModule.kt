package fan.zheyuan.di

import fan.zheyuan.configuration.DatabaseConfiguration
import fan.zheyuan.domain.repository.MessageRepository
import fan.zheyuan.service.MessageService
import fan.zheyuan.utils.DatabaseCheck
import io.ktor.util.KtorExperimentalAPI
import javax.sql.DataSource
import org.koin.dsl.module
import org.koin.experimental.builder.single
import org.springframework.jdbc.core.JdbcTemplate

@KtorExperimentalAPI
val messageModule = module {
    single<MessageRepository>()
    single<MessageService>()
    single<DatabaseCheck>()
    single { JdbcTemplate(get()) }
    single<DataSource> { DatabaseConfiguration(get()).dataSource() }
}