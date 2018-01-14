/**
 * Jenesis Data Store Copyright (c) 2017 Ifunga Ndana. All rights reserved.
 *
 * 1. Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 2. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 3. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * Neither the name Jenesis Data Store nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package io.github.subiyacryolite.jds

import io.github.subiyacryolite.jds.enums.JdsComponent
import io.github.subiyacryolite.jds.enums.JdsComponentType
import io.github.subiyacryolite.jds.enums.JdsImplementation

import java.sql.Connection

/**
 * The PostgreSQL implementation of [JdsDataBase][JdsDb]
 */
abstract class JdsDbPostgreSql : JdsDb(JdsImplementation.POSTGRES, true) {
    override fun tableExists(connection: Connection, tableName: String): Int {
        var toReturn = 0
        val sql = "SELECT COUNT(*) AS Result FROM information_schema.tables WHERE table_catalog = ? AND table_name = ?"
        try {
            connection.prepareStatement(sql).use {
                it.setString(1, connection.catalog)
                it.setString(2, tableName.toLowerCase())
                it.executeQuery().use {
                    while (it.next())
                        toReturn = it.getInt("Result")
                }
            }
        } catch (ex: Exception) {
            toReturn = 0
            ex.printStackTrace(System.err)
        }
        return toReturn
    }

    override fun procedureExists(connection: Connection, procedureName: String): Int {
        var toReturn = 0
        val sql = "select COUNT(*) AS Result from information_schema.routines where routine_catalog = ? and routine_name = ?"
        try {
            connection.prepareStatement(sql).use {
                it.setString(1, connection.catalog)
                it.setString(2, procedureName.toLowerCase())
                it.executeQuery().use {
                    while (it.next())
                        toReturn = it.getInt("Result")
                }
            }
        } catch (ex: Exception) {
            toReturn = 0
            ex.printStackTrace(System.err)
        }
        return toReturn
    }

    override fun viewExists(connection: Connection, viewName: String): Int {
        var toReturn = 0
        val sql = "select COUNT(*) AS Result from information_schema.views where table_catalog = ? and table_name = ?"
        try {
            connection.prepareStatement(sql).use {
                it.setString(1, connection.catalog)
                it.setString(2, viewName.toLowerCase())
                it.executeQuery().use {
                    while (it.next()) {
                        toReturn = it.getInt("Result")
                    }
                }
            }
        } catch (ex: Exception) {
            toReturn = 0
            ex.printStackTrace(System.err)
        }
        return toReturn
    }

    override fun columnExists(connection: Connection, tableName: String, columnName: String): Int {
        var toReturn = 0
        val sql = "SELECT COUNT(COLUMN_NAME) AS Result FROM information_schema.COLUMNS WHERE TABLE_CATALOG = :tableCatalog AND TABLE_NAME = :tableName AND COLUMN_NAME = :columnName"
        toReturn = columnExistsCommonImpl(connection, tableName.toLowerCase(), columnName.toLowerCase(), toReturn, sql)
        return toReturn
    }

    override fun createStoreEntityInheritance(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsEntityInstance.sql")
    }

    override fun createStoreBoolean(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreBoolean.sql")
    }

    override fun createStoreText(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreText.sql")
    }

    override fun createStoreDateTime(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreDateTime.sql")
    }

    override fun createStoreZonedDateTime(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreZonedDateTime.sql")
    }

    override fun createStoreInteger(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreInteger.sql")
    }

    override fun createStoreFloat(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreFloat.sql")
    }

    override fun createStoreDouble(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreDouble.sql")
    }

    override fun createStoreLong(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreLong.sql")
    }

    override fun createStoreTextArray(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreTextArray.sql")
    }

    override fun createStoreDateTimeArray(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreDateTimeArray.sql")
    }

    override fun createStoreIntegerArray(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreIntegerArray.sql")
    }

    override fun createStoreFloatArray(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreFloatArray.sql")
    }

    override fun createStoreDoubleArray(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreDoubleArray.sql")
    }

    override fun createStoreLongArray(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreLongArray.sql")
    }

    override fun createStoreEntities(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsRefEntity.sql")
    }

    override fun createRefEnumValues(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsRefEnum.sql")
    }

    override fun createRefFields(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsRefField.sql")
    }

    override fun createRefFieldTypes(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsRefFieldType.sql")
    }

    override fun createBindEntityFields(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsRefEntityField.sql")
    }

    override fun createBindEntityEnums(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsRefEntityEnum.sql")
    }

    override fun createRefEntityOverview(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/jdsEntityOverview.sql")
    }

    override fun createRefOldFieldValues(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreOldFieldValue.sql")
    }

    override fun createStoreEntityBinding(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsEntityBinding.sql")
    }

    override fun createStoreTime(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreTime.sql")
    }

    override fun createStoreBlob(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsStoreBlob.sql")
    }

    override fun createRefInheritance(connection: Connection) {
        executeSqlFromFile(connection, "sql/postgresql/JdsRefEntityInheritance.sql")
    }

    override fun prepareCustomDatabaseComponents(connection: Connection) {
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.SAVE_BOOLEAN)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.SAVE_BLOB)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.SAVE_TEXT)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.SAVE_LONG)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.SAVE_INTEGER)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.SAVE_FLOAT)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.SAVE_DOUBLE)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.SAVE_DATE_TIME)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.SAVE_TIME)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.SAVE_ZONED_DATE_TIME)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.SAVE_ENTITY_V_3)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.MAP_ENTITY_FIELDS)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.MAP_ENTITY_ENUMS)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.MAP_CLASS_NAME)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.MAP_ENUM_VALUES)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.MAP_FIELD_NAMES)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.MAP_ENTITY_INHERITANCE)
        prepareDatabaseComponent(connection, JdsComponentType.STORED_PROCEDURE, JdsComponent.SAVE_ENTITY_INHERITANCE)
    }

    override fun prepareCustomDatabaseComponents(connection: Connection, jdsComponent: JdsComponent) {
        when (jdsComponent) {
            JdsComponent.SAVE_ENTITY_V_3 -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcStoreEntityOverviewV3.sql")
            JdsComponent.SAVE_ENTITY_INHERITANCE -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcStoreEntityInheritance.sql")
            JdsComponent.MAP_FIELD_NAMES -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcRefField.sql")
            JdsComponent.SAVE_BOOLEAN -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcStoreBoolean.sql")
            JdsComponent.SAVE_BLOB -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcStoreBlob.sql")
            JdsComponent.SAVE_TIME -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcStoreTime.sql")
            JdsComponent.SAVE_TEXT -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcStoreText.sql")
            JdsComponent.SAVE_LONG -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcStoreLong.sql")
            JdsComponent.SAVE_INTEGER -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcStoreInteger.sql")
            JdsComponent.SAVE_FLOAT -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcStoreFloat.sql")
            JdsComponent.SAVE_DOUBLE -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcStoreDouble.sql")
            JdsComponent.SAVE_DATE_TIME -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcStoreDateTime.sql")
            JdsComponent.SAVE_ZONED_DATE_TIME -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcStoreZonedDateTime.sql")
            JdsComponent.MAP_ENTITY_FIELDS -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcRefEntityField.sql")
            JdsComponent.MAP_ENTITY_ENUMS -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcRefEntityEnum.sql")
            JdsComponent.MAP_CLASS_NAME -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcRefEntity.sql")
            JdsComponent.MAP_ENUM_VALUES -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcRefEnum.sql")
            JdsComponent.MAP_ENTITY_INHERITANCE -> executeSqlFromFile(connection, "sql/postgresql/procedures/ProcRefEntityInheritance.sql")
            else -> {
            }
        }
    }

    override fun getDbAddColumnSyntax(): String {
        return "ALTER TABLE %s ADD COLUMN %s %s"
    }

    override fun getDbFloatDataType(): String {
        return "REAL"
    }

    override fun getDbDoubleDataType(): String {
        return "FLOAT"
    }

    override fun getDbZonedDateTimeDataType(): String {
        return "TIMESTAMP WITH TIME ZONE"
    }

    override fun getDbTimeDataType(): String {
        return "TIME WITHOUT TIME ZONE"
    }

    override fun getDbBlobDataType(max: Int): String {
        return "BYTEA"
    }

    override fun getDbIntegerDataType(): String {
        return "INTEGER"
    }

    override fun getDbDateTimeDataType(): String {
        return "TIMESTAMP"
    }

    override fun getDbLongDataType(): String {
        return "BIGINT"
    }

    override fun getDbStringDataType(max: Int): String {
        return if (max == 0)
            "TEXT"
        else
            "VARCHAR($max)"
    }

    override fun getDbBooleanDataType(): String {
        return "BOOLEAN"
    }
}
