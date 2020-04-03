package fan.zheyuan.utils

enum class Status {
    UNKNOWN, UP, DOWN
}

abstract class HealthIndicator {
    abstract suspend fun doHealthCheck(): Status
}