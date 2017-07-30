package io.github.subiyacryolite.jds;

import com.javaworld.NamedPreparedStatement;
import io.github.subiyacryolite.jds.enums.JdsComponent;
import io.github.subiyacryolite.jds.enums.JdsComponentType;
import io.github.subiyacryolite.jds.enums.JdsImplementation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by ifunga on 14/07/2017.
 */
public abstract class JdsDbOracle extends JdsDb {

    public JdsDbOracle() {
        supportsStatements = true;
        implementation = JdsImplementation.ORACLE;
    }

    @Override
    public int tableExists(String tableName) {
        int toReturn = 0;
        String sql = "SELECT COUNT(*) AS Result FROM all_objects WHERE object_type IN ('TABLE') AND object_name = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, tableName.toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                toReturn = resultSet.getInt("Result");
            }
        } catch (Exception ex) {
            toReturn = 0;
            ex.printStackTrace(System.err);
        }
        return toReturn;
    }

    @Override
    public int viewExists(String viewName) {
        int toReturn = 0;
        String sql = "SELECT COUNT(*) AS Result FROM all_objects WHERE object_type IN ('VIEW') AND object_name = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, viewName.toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                toReturn = resultSet.getInt("Result");
            }
        } catch (Exception ex) {
            toReturn = 0;
            ex.printStackTrace(System.err);
        }
        return toReturn;
    }

    @Override
    public int procedureExists(String procedureName) {
        int toReturn = 0;
        String sql = "SELECT COUNT(*) AS Result FROM all_objects WHERE object_type IN ('PROCEDURE') AND object_name = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, procedureName.toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                toReturn = resultSet.getInt("Result");
            }
        } catch (Exception ex) {
            toReturn = 0;
            ex.printStackTrace(System.err);
        }
        return toReturn;
    }

    @Override
    public int columnExists(String tableName, String columnName) {
        int toReturn = 0;
        String sql = "SELECT COUNT(COLUMN_NAME) AS Result FROM ALL_TAB_COLUMNS WHERE TABLE_NAME = :tableName AND COLUMN_NAME = :columnName";
        try (Connection connection = getConnection(); NamedPreparedStatement preparedStatement = new NamedPreparedStatement(connection, sql)) {
            preparedStatement.setString("tableName", tableName.toUpperCase());
            preparedStatement.setString("columnName", columnName.toUpperCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                toReturn = resultSet.getInt("Result");
            }
        } catch (Exception ex) {
            toReturn = 0;
            ex.printStackTrace(System.err);
        }
        return toReturn;
    }

    public String createOrAlterView(String viewName, String viewSql) {
        StringBuilder sb = new StringBuilder("CREATE VIEW\t");
        sb.append(viewName);
        sb.append("\tAS\t");
        sb.append(viewSql);
        String toExecute = sb.toString();
        return toExecute;
    }

    @Override
    protected void createStoreEntityInheritance() {
        executeSqlFromFile("sql/oracle/createStoreEntityInheritance.sql");
    }

    @Override
    protected void createStoreText() {
        executeSqlFromFile("sql/oracle/createStoreText.sql");
    }

    @Override
    protected void createStoreDateTime() {
        executeSqlFromFile("sql/oracle/createStoreDateTime.sql");
    }

    @Override
    protected void createStoreZonedDateTime() {
        executeSqlFromFile("sql/oracle/createStoreZonedDateTime.sql");
    }

    @Override
    protected void createStoreInteger() {
        executeSqlFromFile("sql/oracle/createStoreInteger.sql");
    }

    @Override
    protected void createStoreFloat() {
        executeSqlFromFile("sql/oracle/createStoreFloat.sql");
    }

    @Override
    protected void createStoreDouble() {
        executeSqlFromFile("sql/oracle/createStoreDouble.sql");
    }

    @Override
    protected void createStoreLong() {
        executeSqlFromFile("sql/oracle/createStoreLong.sql");
    }

    @Override
    protected void createStoreTextArray() {
        executeSqlFromFile("sql/oracle/createStoreTextArray.sql");
    }

    @Override
    protected void createStoreDateTimeArray() {
        executeSqlFromFile("sql/oracle/createStoreDateTimeArray.sql");
    }

    @Override
    protected void createStoreIntegerArray() {
        executeSqlFromFile("sql/oracle/createStoreIntegerArray.sql");
    }

    @Override
    protected void createStoreFloatArray() {
        executeSqlFromFile("sql/oracle/createStoreFloatArray.sql");
    }

    @Override
    protected void createStoreDoubleArray() {
        executeSqlFromFile("sql/oracle/createStoreDoubleArray.sql");
    }

    @Override
    protected void createStoreLongArray() {
        executeSqlFromFile("sql/oracle/createStoreLongArray.sql");
    }

    @Override
    protected void createStoreEntities() {
        executeSqlFromFile("sql/oracle/createRefEntities.sql");
    }

    @Override
    protected void createRefEnumValues() {
        executeSqlFromFile("sql/oracle/createRefEnumValues.sql");
    }

    @Override
    protected void createRefFields() {
        executeSqlFromFile("sql/oracle/createRefFields.sql");
    }

    @Override
    protected void createRefFieldTypes() {
        executeSqlFromFile("sql/oracle/createRefFieldTypes.sql");
    }

    @Override
    protected void createBindEntityFields() {
        executeSqlFromFile("sql/oracle/createBindEntityFields.sql");
    }

    @Override
    protected void createBindEntityEnums() {
        executeSqlFromFile("sql/oracle/createBindEntityEnums.sql");
    }

    @Override
    protected void createRefEntityOverview() {
        executeSqlFromFile("sql/oracle/createStoreEntityOverview.sql");
    }

    @Override
    protected void createRefOldFieldValues() {
        executeSqlFromFile("sql/oracle/createStoreOldFieldValues.sql");
    }

    @Override
    protected void createStoreEntityBinding() {
        executeSqlFromFile("sql/oracle/createStoreEntityBinding.sql");
    }

    protected void createStoreTime() {
        executeSqlFromFile("sql/oracle/createStoreTime.sql");
    }

    protected void createStoreBlob() {
        executeSqlFromFile("sql/oracle/createStoreBlob.sql");
    }

    @Override
    protected void createRefInheritance() {
        executeSqlFromFile("sql/oracle/createRefInheritance.sql");
    }

    @Override
    protected void prepareCustomDatabaseComponents() {
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.SaveBlob);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.SaveText);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.SaveLong);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.SaveInteger);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.SaveFloat);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.SaveDouble);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.SaveDateTime);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.SaveTime);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.SaveZonedDateTime);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.SaveEntityV2);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.MapEntityFields);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.MapEntityEnums);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.MapClassName);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.MapEnumValues);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.MapFieldNames);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.MapFieldTypes);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.MapEntityInheritance);
        prepareDatabaseComponent(JdsComponentType.STORED_PROCEDURE, JdsComponent.SaveEntityInheritance);
    }

    @Override
    protected void prepareCustomDatabaseComponents(JdsComponent jdsComponent) {
        switch (jdsComponent) {
            case SaveEntityV2:
                executeSqlFromFile("sql/oracle/procedures/procStoreEntityOverviewV2.sql");
                break;
            case SaveEntityInheritance:
                executeSqlFromFile("sql/oracle/procedures/procStoreEntityInheritance.sql");
                break;
            case MapFieldNames:
                executeSqlFromFile("sql/oracle/procedures/procBindFieldNames.sql");
                break;
            case MapFieldTypes:
                executeSqlFromFile("sql/oracle/procedures/procBindFieldTypes.sql");
                break;
            case SaveBlob:
                executeSqlFromFile("sql/oracle/procedures/procStoreBlob.sql");
                break;
            case SaveTime:
                executeSqlFromFile("sql/oracle/procedures/procStoreTime.sql");
                break;
            case SaveText:
                executeSqlFromFile("sql/oracle/procedures/procStoreText.sql");
                break;
            case SaveLong:
                executeSqlFromFile("sql/oracle/procedures/procStoreLong.sql");
                break;
            case SaveInteger:
                executeSqlFromFile("sql/oracle/procedures/procStoreInteger.sql");
                break;
            case SaveFloat:
                executeSqlFromFile("sql/oracle/procedures/procStoreFloat.sql");
                break;
            case SaveDouble:
                executeSqlFromFile("sql/oracle/procedures/procStoreDouble.sql");
                break;
            case SaveDateTime:
                executeSqlFromFile("sql/oracle/procedures/procStoreDateTime.sql");
                break;
            case SaveZonedDateTime:
                executeSqlFromFile("sql/oracle/procedures/procStoreZonedDateTime.sql");
                break;
            case SaveEntity:
                executeSqlFromFile("sql/oracle/procedures/procStoreEntityOverview.sql");
                break;
            case MapEntityFields:
                executeSqlFromFile("sql/oracle/procedures/procBindEntityFields.sql");
                break;
            case MapEntityEnums:
                executeSqlFromFile("sql/oracle/procedures/procBindEntityEnums.sql");
                break;
            case MapClassName:
                executeSqlFromFile("sql/oracle/procedures/procRefEntities.sql");
                break;
            case MapEnumValues:
                executeSqlFromFile("sql/oracle/procedures/procRefEnumValues.sql");
                break;
            case MapEntityInheritance:
                executeSqlFromFile("sql/oracle/procedures/procBindParentToChild.sql");
                break;
        }
    }
}
