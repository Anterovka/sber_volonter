package sbp.sbt.idempotence.model.interfaces;

import com.sbt.model.base.Entity;

import java.util.Date;


public interface IdempotenceEntityApiCall extends Entity {

    String API_CALL = "ApiCall";

    String getApiCallId();

    void setApiCallId(String apiCallId);

    Date getFirstCallDate();

    void setFirstCallDate(Date firstCallDate);

    String getData();

    void setData(String data);

    String getBigData();

    void setBigData(String data);
}
