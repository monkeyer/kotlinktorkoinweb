package fan.zheyuan.domain.model

import java.io.Serializable

data class Video(val id: Long, val title: String, val authorId: String, val videoFileName: String) : Serializable