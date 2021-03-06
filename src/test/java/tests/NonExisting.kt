package tests

import common.BaseTestConfig
import entities.Example
import io.github.subiyacryolite.jds.JdsLoad
import io.github.subiyacryolite.jds.enums.JdsFilterBy
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class NonExisting : BaseTestConfig("Load non-existing items") {

    @Throws(Exception::class)
    fun loadNonExisting() {
        val load = JdsLoad(jdsDb, Example::class.java, JdsFilterBy.UUID, setOf("DOES_NOT_EXIST"))
        val process = Executors.newSingleThreadExecutor().submit(load)
        while (!process.isDone)
            Thread.sleep(16)
        println(process.get())
    }

    @Test
    @Throws(Exception::class)
    fun testSqLite() {
        initialiseSqLiteBackend()
        loadNonExisting()
    }

    @Test
    @Throws(Exception::class)
    fun testMySql() {
        initialiseMysqlBackend()
        loadNonExisting()
    }

    @Test
    @Throws(Exception::class)
    fun testMariaDb() {
        initialiseMariaDbBackend()
        loadNonExisting()
    }

    @Test
    @Throws(Exception::class)
    fun testPostgreSql() {
        initialisePostgeSqlBackend()
        loadNonExisting()
    }

    @Test
    @Throws(Exception::class)
    fun testTransactionalSql() {
        initialiseTSqlBackend()
        loadNonExisting()
    }

    @Test
    @Throws(Exception::class)
    fun testOracle() {
        initialiseOracleBackend()
        loadNonExisting()
    }

    @Test
    @Throws(Exception::class)
    fun allImplementations() {
        testSqLite()
        testMySql()
        testPostgreSql()
        testTransactionalSql()
        testOracle()
        testMariaDb()
    }
}