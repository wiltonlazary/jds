package io.github.subiyacryolite.jds.events;

import com.javaworld.INamedStatement;
import com.javaworld.NamedCallableStatement;
import com.javaworld.NamedPreparedStatement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by ifunga on 13/05/2017.
 */
public class OnPostLoadEventArguments {
    private final String entityGuid;
    private final Connection connection;
    private final LinkedHashMap <String, PreparedStatement> statements;
    private final LinkedHashMap <String, CallableStatement> calls;
    private final LinkedHashMap<String, INamedStatement> namedStatements;
    private final LinkedHashMap<String, INamedStatement> namedCalls;

    public OnPostLoadEventArguments(Connection connection, String entityGuid) {
        this.entityGuid = entityGuid;
        this.connection = connection;
        this.statements = new LinkedHashMap <>();
        this.calls = new LinkedHashMap<>();
        this.namedStatements = new LinkedHashMap<>();
        this.namedCalls = new LinkedHashMap<>();
    }

    public String getEntityGuid() {
        return entityGuid;
    }

    public Connection getConnection() {
        return connection;
    }

    public synchronized PreparedStatement getOrAddStatement(String key) throws SQLException {
        if (!statements.containsKey(key))
            statements.put(key, connection.prepareStatement(key));
        return statements.get(key);
    }

    public synchronized CallableStatement getOrAddCall(String key) throws SQLException {
        if (!calls.containsKey(key))
            calls.put(key, connection.prepareCall(key));
        return calls.get(key);
    }

    public synchronized INamedStatement getOrAddNamedStatement(String key) throws SQLException {
        if (!namedStatements.containsKey(key))
            namedStatements.put(key, new NamedPreparedStatement(connection, key));
        return namedStatements.get(key);
    }

    public synchronized INamedStatement getOrAddNamedCall(String key) throws SQLException {
        if (!namedCalls.containsKey(key))
            namedCalls.put(key, new NamedCallableStatement(connection, key));
        return namedCalls.get(key);
    }

    public void executeBatches() throws SQLException {
        for (PreparedStatement preparedStatement : statements.values()) {
            preparedStatement.executeBatch();
            preparedStatement.close();
        }
        for (CallableStatement callableStatement : calls.values()) {
            callableStatement.executeBatch();
            callableStatement.close();
        }
        for (INamedStatement preparedStatement : namedStatements.values()) {
            preparedStatement.executeBatch();
            preparedStatement.close();
        }
        for (INamedStatement callableStatement : namedCalls.values()) {
            callableStatement.executeBatch();
            callableStatement.close();
        }
    }
}