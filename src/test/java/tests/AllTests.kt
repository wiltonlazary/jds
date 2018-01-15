package tests

import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


class AllTests {

    val bulkSaveAndLoad = BulkSaveAndLoad()
    val customReport = CustomReport()
    val customReportJson = CustomReportJson()
    val inheritance = Inheritance()
    val legacyValidation = LegacyValidation()
    val legacyLoadAndSave = LegacyLoadAndSave()
    val nonExisting = NonExisting()
    val timeConstructs = TimeConstructs()
    val loadAndSaveTests = LoadAndSaveTests()

    @Test
    fun testAll() {
        testTransactionalSql()
        testMySql()
        testOracle()
        testPostgreSql()
        testSqLite()
    }

    @Test
    fun testTransactionalSql() {
        bulkSaveAndLoad.testTransactionalSql()
        customReport.testTransactionalSql()
        customReportJson.testTransactionalSql()
        inheritance.testTransactionalSql()
        legacyValidation.testTransactionalSql()
        legacyLoadAndSave.testTransactionalSql()
        nonExisting.testTransactionalSql()
        timeConstructs.testTransactionalSql()
        loadAndSaveTests.testTransactionalSql()
    }

    @Test
    fun testMySql() {
        bulkSaveAndLoad.testMySql()
        customReport.testMySql()
        customReportJson.testMySql()
        inheritance.testMySql()
        legacyValidation.testMySql()
        legacyLoadAndSave.testMySql()
        nonExisting.testMySql()
        timeConstructs.testMySql()
        loadAndSaveTests.testMySql()
    }

    @Test
    fun testOracle() {
        bulkSaveAndLoad.testOracle()
        customReport.testOracle()
        customReportJson.testOracle()
        inheritance.testOracle()
        legacyValidation.testOracle()
        legacyLoadAndSave.testOracle()
        nonExisting.testOracle()
        timeConstructs.testOracle()
        loadAndSaveTests.testOracle()
    }

    @Test
    fun testPostgreSql() {
        bulkSaveAndLoad.testPostgreSql()
        customReport.testPostgreSql()
        customReportJson.testPostgreSql()
        inheritance.testPostgreSql()
        legacyValidation.testPostgreSql()
        legacyLoadAndSave.testPostgreSql()
        nonExisting.testPostgreSql()
        timeConstructs.testPostgreSql()
        loadAndSaveTests.testPostgreSql()
    }

    @Test
    fun testSqLite() {
        bulkSaveAndLoad.testSqLite()
        customReport.testSqLite()
        customReportJson.testSqLite()
        inheritance.testSqLite()
        legacyValidation.testSqLite()
        legacyLoadAndSave.testSqLite()
        nonExisting.testSqLite()
        timeConstructs.testSqLite()
        loadAndSaveTests.testSqLite()
    }

    @Test
    fun javaTime() {
        //1969-04-04 00:00:00.000
        val ticks = 621120960000000000L//date of birth
        println(LocalDateTime.ofInstant(DateHelper.getDate(ticks), ZoneId.of("UTC")))
    }


    internal object DateHelper {

        private val TICKS_AT_EPOCH = 621355968000000000L
        private val TICKS_PER_MILLISECOND: Long = 10000

        fun getDate(UTCTicks: Long): Instant = Instant.ofEpochMilli((UTCTicks - TICKS_AT_EPOCH) / TICKS_PER_MILLISECOND)

    }
}