package fan.zheyuan.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.*


val appJacksonMapper: ObjectMapper = jacksonObjectMapper().apply {
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    setSerializationInclusion(JsonInclude.Include.NON_NULL)
    setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))
    registerModule(JavaTimeModule())
}