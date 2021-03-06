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
package io.github.subiyacryolite.jds.events

import io.github.subiyacryolite.jds.IJdsDb
import java.sql.Connection
import java.sql.SQLException
import java.util.concurrent.ConcurrentMap

/**
 * Event arguments to handle this listener invocation.
 * This class supports batching via the use of the [getOrAddCall(String)][getOrAddCall],
 * [getOrAddStatement(String)][getOrAddStatement], [getOrAddNamedCall(String)][getOrAddNamedCall] and
 * [getOrAddNamedStatement(String)][getOrAddNamedStatement] methods.
 */
open class SaveEventArgument(jdsDb: IJdsDb, connection: Connection, alternateConnection: ConcurrentMap<Int, Connection>) : EventArgument(jdsDb, connection, alternateConnection) {

    override fun executeBatches() = try {
        connection.autoCommit = false
        alternateConnections.forEach { it.value.autoCommit = false }
        statements.values.forEach { it.executeBatch() }
        connection.commit()
        alternateConnections.forEach { it.value.commit() }
    } catch (exception: Exception) {
        connection.rollback()
        alternateConnections.forEach { it.value.rollback() }
        exception.printStackTrace(System.err)
    } finally {
        connection.autoCommit = true
        alternateConnections.forEach { it.value.autoCommit = true }
    }

    @Throws(SQLException::class)
    fun closeStatements() {
        statements.values.forEach { it.close() }
    }
}