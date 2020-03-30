package fan.zheyuan.ktorkoin

import org.koin.dsl.module
import org.koin.experimental.builder.single
import org.koin.experimental.builder.singleBy

val helloAppModule = module(createdAtStart = true) {
    singleBy<HelloService, HelloServiceImpl>()

    single<HelloRepository>()
}