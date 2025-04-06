package sbp.com.sbt.dataspace.feather.common;

/**
 * Pointer to object
 *
 * @param <O> Object type
 */
public class Pointer<O> {

    public O object;

    public Pointer() {
    }

    /**
     * @param object Объект
     */
    public Pointer(O object) {
        this.object = object;
    }
}
