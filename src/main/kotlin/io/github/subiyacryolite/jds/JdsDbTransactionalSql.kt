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
import java.util.*

/**
 * The TSQL implementation of [JdsDataBase][JdsDb]
 */
abstract class JdsDbTransactionalSql : JdsDb(JdsImplementation.TSQL, true) {

    override fun tableExists(connection: Connection, tableName: String): Int {
        var toReturn = 0
        val sql = "SELECT COUNT(*) AS Result FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = ?"
        try {
            connection.prepareStatement(sql).use {
                it.setString(1, tableName)
                it.executeQuery().use {
                    while (it.next())
                        toReturn = it.getInt("Result")
                }
            }
        } catch (ex: Exception) {
            toReturn = 0
            ex.printStackTrace(System.err)
        }
        return when (toReturn >= 1) {
            true -> 1
            false -> 0
        }
    }

    override fun procedureExists(connection: Connection, procedureName: String): Int {
        var toReturn = 0
        val sql = "SELECT COUNT(*) AS Result FROM sysobjects WHERE NAME = ? AND XTYPE = ?"
        try {
            connection.prepareStatement(sql).use {
                it.setString(1, procedureName)
                it.setString(2, "P")
                it.executeQuery().use {
                    while (it.next())
                        toReturn = it.getInt("Result")
                }
            }
        } catch (ex: Exception) {
            toReturn = 0
            ex.printStackTrace(System.err)
        }
        return when (toReturn >= 1) {
            true -> 1
            false -> 0
        }
    }

    override fun triggerExists(connection: Connection, triggerName: String): Int {
        var toReturn = 0
        val sql = "SELECT COUNT(*) AS Result FROM sysobjects WHERE NAME = ? AND XTYPE = ?"
        try {
            connection.prepareStatement(sql).use {
                it.setString(1, triggerName)
                it.setString(2, "TR")
                it.executeQuery().use {
                    while (it.next())
                        toReturn = it.getInt("Result")
                }
            }
        } catch (ex: Exception) {
            toReturn = 0
            ex.printStackTrace(System.err)
        }
        return when (toReturn >= 1) {
            true -> 1
            false -> 0
        }
    }

    override fun viewExists(connection: Connection, viewName: String): Int {
        var toReturn = 0
        val sql = "SELECT COUNT(*) AS Result FROM sysobjects WHERE NAME = ? AND XTYPE = ?"
        try {
            connection.prepareStatement(sql).use {
                it.setString(1, viewName)
                it.setString(2, "V")
                it.executeQuery().use {
                    while (it.next())
                        toReturn = it.getInt("Result")
                }
            }
        } catch (ex: Exception) {
            toReturn = 0
            ex.printStackTrace(System.err)
        }
        return when (toReturn >= 1) {
            true -> 1
            false -> 0
        }
    }

    override fun columnExists(connection: Connection, tableName: String, columnName: String): Int {
        var toReturn = 0
        val sql = "SELECT COUNT(COLUMN_NAME) AS Result from INFORMATION_SCHEMA.columns WHERE TABLE_CATALOG = :tableCatalog and TABLE_NAME = :tableName and COLUMN_NAME = :columnName"
        toReturn = columnExistsCommonImpl(connection, tableName, columnName, toReturn, sql)
        return toReturn
    }

    override fun createStoreEntities(connection: Connection) {
        executeSqlFromFile(connection, "sql/tsql/jds_ref_entity.sql")
    }

    override fun createRefEnumValues(connection: Connection) {
        executeSqlFromFile(connection, "sql/tsql/jds_ref_enum.sql")
    }

    override fun createRefFields(connection: Connection) {
        executeSqlFromFile(connection, "sql/tsql/jds_ref_field.sql")
    }

    override fun createRefFieldTypes(connection: Connection) {
        executeSqlFromFile(connection, "sql/tsql/jds_ref_field_type.sql")
    }

    override fun createBindEntityFields(connection: Connection) {
        executeSqlFromFile(connection, "sql/tsql/jds_ref_entity_field.sql")
    }

    override fun createBindEntityEnums(connection: Connection) {
        executeSqlFromFile(connection, "sql/tsql/jds_ref_entity_enum.sql")
    }

    override fun createRefEntityOverview(connection: Connection) {
        executeSqlFromFile(connection, "sql/tsql/jds_entity_overview.sql")
    }

    override fun createBindEntityBinding(connection: Connection) {
        executeSqlFromFile(connection, "sql/tsql/jds_entity_binding.sql")
    }

    override fun createRefInheritance(connection: Connection) {
        executeSqlFromFile(connection, "sql/tsql/jds_ref_entity_inheritance.sql")
    }

    override fun getNativeDataTypeFloat(): String {
        return "REAL"
    }

    override fun getNativeDataTypeDouble(): String {
        return "FLOAT"
    }

    override fun getNativeDataTypeZonedDateTime(): String {
        return "DATETIMEOFFSET(7)"
    }

    override fun getNativeDataTypeTime(): String {
        return "TIME(7)"
    }

    override fun getNativeDataTypeBlob(max: Int): String {
        return return if (max == 0)
            "VARBINARY(MAX)"
        else
            "VARBINARY($max)"
    }

    override fun getNativeDataTypeInteger(): String {
        return "INTEGER"
    }

    override fun getNativeDataTypeDate(): String{
        return "DATE"
    }

    override fun getNativeDataTypeDateTime(): String {
        return "DATETIME"
    }

    override fun getNativeDataTypeLong(): String {
        return "BIGINT"
    }

    override fun getNativeDataTypeString(max: Int): String {
        return if (max == 0)
            "NVARCHAR(MAX)"
        else
            "NVARCHAR($max)"
    }

    override fun getNativeDataTypeBoolean(): String {
        return "BIT"
    }

    override fun getDbCreateIndexSyntax(tableName: String, columnName: String, indexName: String): String {
        return "CREATE INDEX $indexName ON $tableName($columnName);"
    }

    override fun createOrAlterProc(procedureName: String,
                                   tableName: String,
                                   columns: Map<String, String>,
                                   uniqueColumns: Collection<String>,
                                   doNothingOnConflict: Boolean): String {
        val sqlBuilder = StringBuilder()

        sqlBuilder.append("CREATE OR ALTER PROCEDURE $procedureName(")
        val inputParameters = StringJoiner(", ")
        columns.forEach { column, type -> inputParameters.add("@p_$column $type") }
        sqlBuilder.append(inputParameters)
        sqlBuilder.append(")\n")

        sqlBuilder.append("\t AS\n")
        sqlBuilder.append("\t\tBEGIN\n")
        sqlBuilder.append("\t\t\tMERGE  $tableName AS dest\n")

        sqlBuilder.append("\t\t\tUSING (VALUES(")
        val parameterColumns = StringJoiner(", ")
        columns.forEach { column, _ -> parameterColumns.add("@p_$column") }
        sqlBuilder.append(parameterColumns)
        sqlBuilder.append("))")

        sqlBuilder.append("AS src(")
        val targetColumns = StringJoiner(", ")
        columns.forEach { column, _ -> targetColumns.add(column) }
        sqlBuilder.append(targetColumns)
        sqlBuilder.append(")\n")

        sqlBuilder.append("\t\t\tON (")
        val comparisonColumns = StringJoiner(" AND ")
        uniqueColumns.forEach { column -> comparisonColumns.add("src.$column = dest.$column") }
        sqlBuilder.append(comparisonColumns)
        sqlBuilder.append(")\n")

        sqlBuilder.append("\t\t\tWHEN NOT MATCHED THEN INSERT (")
        val notMatchedColumns = StringJoiner(", ")
        columns.forEach { column, _ -> notMatchedColumns.add(column) }
        sqlBuilder.append(notMatchedColumns)
        sqlBuilder.append(")")

        sqlBuilder.append(" VALUES(")
        val notMatchedColumnsSrc = StringJoiner(", ")
        columns.forEach { column, _ -> notMatchedColumnsSrc.add("src.$column") }
        sqlBuilder.append(notMatchedColumnsSrc)
        sqlBuilder.append(")\n")

        if (!doNothingOnConflict && columns.count() > uniqueColumns.count()) {
            sqlBuilder.append("\t\t\tWHEN MATCHED THEN UPDATE SET ")
            val comparisonColumns = StringJoiner(", ")
            columns.forEach { column, _ ->
                if (!uniqueColumns.contains(column)) //dont update unique columns
                    comparisonColumns.add("dest.$column = src.$column")
            }
            sqlBuilder.append(comparisonColumns)
        }
        sqlBuilder.append("\t\t\t;\n")
        sqlBuilder.append("\t\tEND")
        return sqlBuilder.toString()
    }
}
