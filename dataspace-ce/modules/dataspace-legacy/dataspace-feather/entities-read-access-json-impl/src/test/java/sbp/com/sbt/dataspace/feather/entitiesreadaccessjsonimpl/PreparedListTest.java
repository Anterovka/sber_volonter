package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrap;

@DisplayName("Testing of the prepared list")
public class PreparedListTest {

    static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    @DisplayName("Test in multithreaded environment conditions")
    @Test
    public void multiThreadTest() {
        PreparedList<Integer> list = new PreparedList<>(index -> 0, 1);
        list.list = new TestList<>(Thread.currentThread());
        Future<Integer> future = EXECUTOR_SERVICE.submit(() -> list.get(2));
        list.get(1);
        wrap(future::get);
        assertEquals(3, list.list.size());
    }
}
