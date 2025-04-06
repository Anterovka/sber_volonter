package sbp.com.sbt.dataspace.feather.entitiesreadaccessjsonimpl;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

import static sbp.com.sbt.dataspace.feather.common.CommonHelper.wrap;

/**
 * Test list
 *
 * @param <E> Element type
 */
class TestList<E> extends ArrayList<E> {

    Thread mainThread;
    CyclicBarrier cyclicBarrier;
    boolean flag;

    /**
     * @param mainThread Main thread
     */
    TestList(Thread mainThread) {
        this.mainThread = mainThread;
        cyclicBarrier = new CyclicBarrier(2, () -> cyclicBarrier = null);
    }

    @Override
    public int size() {
        int result = super.size();
        if (cyclicBarrier != null) {
            if (Thread.currentThread() == mainThread) {
                if (!flag) {
                    flag = true;
                } else {
                    wrap(() -> cyclicBarrier.await());
                }
            } else {
                wrap(() -> cyclicBarrier.await());
            }
        }
        return result;
    }
}
