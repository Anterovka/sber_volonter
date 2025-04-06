package sbp.sbt.idempotence.model.interfaces;

public interface AggregateIdempotenceEntityApiCall<T> extends IdempotenceEntityApiCall {
    T getParentObject();

    void setParentObject(T parentObject);
}
