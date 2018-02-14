package tests

import common.BaseTestConfig
import constants.Fields
import constants.Rights
import entities.*
import io.github.subiyacryolite.jds.JdsSave
import io.github.subiyacryolite.jds.JdsTable
import io.github.subiyacryolite.jds.enums.JdsFilterBy
import org.junit.jupiter.api.Test

class CustomReportAndSharedConnections : BaseTestConfig("Custom reports and shared connections") {

    @Throws(Exception::class)
    private fun test() {
        val customTable = JdsTable()
        customTable.uniqueEntries = true
        customTable.name = "CrtAddressSpecific"
        customTable.registerEntity(Address::class.java)
        customTable.registerField(Fields.AREA_NAME)
        customTable.registerField(Fields.CITY_NAME)
        customTable.uniqueBy = JdsFilterBy.UUID

        val crtAddress = JdsTable(Address::class.java, true)
        crtAddress.uniqueBy = JdsFilterBy.UUID

        val crtAddressBook = JdsTable(AddressBook::class.java, true)
        val crtLogin = JdsTable(Login::class.java, true)
        val crtA = JdsTable(EntityA::class.java, true)
        val crtB = JdsTable(EntityB::class.java, true)
        val crtC = JdsTable(EntityC::class.java, true)
        val exampleType = JdsTable(Example::class.java, true)

        jdsDb.mapTable(customTable, crtAddress, crtAddressBook, crtLogin, crtA, crtB, crtC, exampleType)
        jdsDb.prepareTables()

        val login1 = Login()
        login1.overview.uuid = "login1"
        login1.rights.add(Rights.CAN_LOGIN)
        login1.rights.add(Rights.CAN_CREATE_USER)

        val login2 = Login()
        login2.overview.uuid = "login2"
        login2.rights.add(Rights.CAN_LOGIN)
        login2.rights.add(Rights.CAN_DELETE_RECORD)

        jdsDb.getConnection().use {
            val saveCollection = JdsSave(jdsDb, collection, it)
            saveCollection.closeConnection = false
            saveCollection.call()

            val saveLogins = JdsSave(jdsDb, listOf(login1, login2), it)
            saveLogins.closeConnection = false
            saveLogins.call()

            val saveAddressBook = JdsSave(jdsDb, listOf(addressBook), it)
            saveAddressBook.closeConnection = false
            saveAddressBook.call()

            val saveInheritedObjects = JdsSave(jdsDb, inheritanceCollection, it)
            saveInheritedObjects.closeConnection = false
            saveInheritedObjects.call()
        }
    }

    @Test
    @Throws(Exception::class)
    fun testPostgreSql() {
        initialisePostgeSqlBackend()
        test()
    }

    @Test
    @Throws(Exception::class)
    fun testTransactionalSql() {
        initialiseTSqlBackend()
        test()
    }

    @Test
    @Throws(Exception::class)
    fun testSqLite() {
        initialiseSqLiteBackend()
        test()
    }

    @Test
    @Throws(Exception::class)
    fun testMySql() {
        initialiseMysqlBackend()
        test()
    }

    @Test
    @Throws(Exception::class)
    fun testMariaDb() {
        initialiseMariaDbBackend()
        test()
    }

    @Test
    @Throws(Exception::class)
    fun testOracle() {
        initialiseOracleBackend()
        test()
    }

    @Test
    @Throws(Exception::class)
    fun allImplementations() {
        testTransactionalSql()
        testPostgreSql()
        testMySql()
        testOracle()
        testSqLite()
        testMariaDb()
    }
}