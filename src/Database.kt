package fan.zheyuan

import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import org.ehcache.CacheManagerBuilder
import org.ehcache.config.CacheConfigurationBuilder
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class Database(val uploadDir: File) {
    val gson = GsonBuilder()
        .disableHtmlEscaping()
        .serializeNulls()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .create()

    val cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true)

    val videosCache = cacheManager.createCache<Long, Video>(
        "videos",
        CacheConfigurationBuilder.newCacheConfigurationBuilder<Long, Video>()
            .buildConfig(Class.forName("java.lang.Long") as Class<Long>, Video::class.java)
    )

    private val digitsOnlyRegex = "\\d+".toRegex()
    private val allIds by lazy {

        uploadDir.listFiles { f ->
            f.extension == "idx" && f.nameWithoutExtension.matches(digitsOnlyRegex)
        }.mapTo(ArrayList()) {
            it.nameWithoutExtension.toLong()
        }
    }

    val biggestId by lazy { AtomicLong(allIds.max() ?: 0) }

    fun listAll(): Sequence<Video> = allIds.asSequence().mapNotNull { videoById(it) }

    fun top() = listAll().take(10).toList()

    fun videoById(id: Long): Video? {
        val video = videosCache.get(id)
        if (video != null) {
            return video
        }

        return try {
            val json = gson.fromJson(File(uploadDir, "$id.idx").readText(), Video::class.java)
            videosCache.put(id, json)
            json
        } catch (e: Throwable) {
            null
        }
    }

    fun nextId() = biggestId.incrementAndGet()

    fun addVideo(title: String, userId: String, file: File): Long {
        val id = nextId()
        val video = Video(id, title, userId, file.path)

        File(uploadDir, "$id.idx").writeText(gson.toJson(video))
        allIds.add(id)

        videosCache.put(id, video)

        return id
    }
}