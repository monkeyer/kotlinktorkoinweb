package fan.zheyuan.configuration

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.ApplicationEnvironment

class DatabaseConfiguration (private val env: ApplicationEnvironment) {
    private val hikariConfig = HikariConfig().apply {
        isAutoCommit = true
        jdbcUrl = env.config.property("ktor.datasource.jdbcUrl").getString()
        minimumIdle = env.config.property("ktor.datasource.minIdle").getString().toInt()
        driverClassName = env.config.property("ktor.datasource.driverClassName").getString()
        maximumPoolSize = env.config.property("ktor.datasource.maxPoolSize").getString().toInt()
        connectionTestQuery = env.config.property("ktor.datasource.connectionTestQuery").getString()
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    }

    fun dataSource(): HikariDataSource = HikariDataSource(hikariConfig)
}