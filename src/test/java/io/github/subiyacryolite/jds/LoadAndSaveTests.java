package io.github.subiyacryolite.jds;

import io.github.subiyacryolite.jds.common.BaseTestConfig;
import io.github.subiyacryolite.jds.entities.JdsExample;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by ifunga on 12/04/2017.
 */
public class LoadAndSaveTests extends BaseTestConfig {

    @Test
    public void callableSqlLiteBulkSave() throws Exception {
        initialiseTSqlBackend();
        bulkSave();
    }

    @Test
    public void callableSqlLiteBulkLoad() throws ExecutionException, InterruptedException {
        initialiseMysqlBackend();
        load();
    }

    @Test
    public void callableSqlLiteBulkLoadSave() throws Exception {
        initialiseSqlLiteBackend();
        bulkSave();
        load();
    }

    @Test
    public void callableSqlLiteLoadSave() throws Exception {
        initialiseSqlLiteBackend();
        save();
        load();
    }

    @Test
    public void callableMysqlLoadSave() throws Exception {
        initialiseMysqlBackend();
        save();
        load();
    }

    @Test
    public void callablePostgeSqlLoadSave() throws Exception {
        initialisePostgeSqlBackend();
        save();
        load();
    }

    @Test
    public void callableTSqlLoadSave() throws Exception {
        initialiseTSqlBackend();
        save();
        load();
    }

    @Test
    public void save() throws Exception {
        List<JdsExample> collection = getCollection();
        Callable<Boolean> save = new JdsSave(jdsDb, collection);
        FutureTask<Boolean> saving = new FutureTask(save);
        saving.run();
        while (!saving.isDone())
            System.out.println("Waiting for operation 1 to complete");
        System.out.printf("Saved? %s\n", saving.get());
    }

    @Test
    public void bulkSave() throws Exception {
        List<JdsExample> collection = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            JdsExample jdsExample = new JdsExample();
            jdsExample.setEntityGuid("guid_" + i);
            jdsExample.setIntField(i);
            jdsExample.setFloatField(i + 1);
            jdsExample.setDoubleField(i + 2);
            jdsExample.setLongField(i + 3);
            collection.add(jdsExample);
        }
        Callable<Boolean> save = new JdsSave(jdsDb, collection);
        FutureTask<Boolean> saving = new FutureTask(save);
        saving.run();
        while (!saving.isDone())
            System.out.println("Waiting for operation 1 to complete");
        System.out.printf("Saved? %s\n", saving.get());
    }

    @Test
    public void load() throws ExecutionException, InterruptedException {
        Callable<List<JdsExample>> loadAllInstances = new JdsLoad(jdsDb, JdsExample.class);
        Callable<List<JdsExample>> loadSpecificInstance = new JdsLoad(jdsDb, JdsExample.class, "instance3");
        Callable<List<JdsExample>> loadSortedInstances = new JdsLoad(jdsDb, JdsExample.class);

        FutureTask<List<JdsExample>> loadingAllInstances = new FutureTask(loadAllInstances);
        FutureTask<List<JdsExample>> loadingSpecificInstance = new FutureTask(loadSpecificInstance);
        FutureTask<List<JdsExample>> loadingSortedInstances = new FutureTask(loadSortedInstances);
        loadingAllInstances.run();
        loadingSpecificInstance.run();
        loadingSortedInstances.run();

        while (!loadingAllInstances.isDone())
            System.out.println("Waiting for operation 1 to complete");
        while (!loadingSpecificInstance.isDone())
            System.out.println("Waiting for operation 2 to complete");
        while (!loadingSortedInstances.isDone())
            System.out.println("Waiting for operation 3 to complete");

        List<JdsExample> allInstances = loadingAllInstances.get();
        List<JdsExample> specificInstance = loadingSpecificInstance.get();
        List<JdsExample> sortedInstances = loadingSortedInstances.get();

        System.out.println(allInstances);
        System.out.println(specificInstance);
        System.out.println(sortedInstances);

        System.out.println("DONE");
    }

    @Test
    public void isolatedDelete() throws ExecutionException, InterruptedException {
        Callable<Boolean> delete = new JdsDelete(jdsDb, "instance2");
        FutureTask<Boolean> deleting = new FutureTask(delete);
        deleting.run();
        while (!deleting.isDone())
            System.out.println("Waiting for operation to complete");
        System.out.println("Deleted? " + deleting.get());
    }
}