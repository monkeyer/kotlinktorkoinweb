package fan.zheyuan

import fan.zheyuan.ktorkoin.helloAppModule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.test.check.checkModules

@Category(CheckModulesTest::class)
class CheckModulesTest {
    @Test
    fun `check modules`() = checkModules {
        modules(helloAppModule)
    }
}