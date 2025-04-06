package sbp.com.sbt.dataspace.extension.status;

import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.exception.checkmodel.ClassNotFoundException;
import com.sbt.status.xml.XmlGroup;
import com.sbt.status.xml.XmlStatus;
import com.sbt.status.xml.XmlStatusTo;
import com.sbt.status.xml.XmlStatuses;

import java.util.Objects;
import java.util.Optional;

public class StatusUtils {

    private StatusUtils() {}

    public static boolean isModelHave(XmlModel model, XmlGroup group) {
        String className = group.getModelClass().getName();
        Optional<XmlModelClass> first = model.getClassesAsList().stream()
            .filter(it -> Objects.equals(it.getName(), className))
            .findFirst();
        if (first.isEmpty()) {
            throw new ClassNotFoundException(className);
        }
        XmlModelClass xmlModelClass = first.get();
        if (Objects.isNull(xmlModelClass.getStatuses())) {
            return false;
        }
        return xmlModelClass.getStatuses().getGroups().stream()
            .anyMatch(it -> Objects.equals(it.getCode(), group.getCode()));
    }

    public static boolean isModelDoesNotHave(XmlModel model, XmlGroup group) {
        return !isModelHave(model, group);
    }

    public static boolean isModelHave(XmlModel model, XmlStatus status) {
        XmlGroup group = status.getGroup();
        String className = group.getModelClass().getName();
        String groupCode = group.getCode();
        XmlModelClass modelClass = model.getClass(className);
        XmlGroup xmlGroup = modelClass.getStatuses().getGroups().stream()
            .filter(it -> Objects.equals(it.getCode(), groupCode))
            .findFirst()
            .orElseThrow();
        return xmlGroup.getStatuses().stream()
            .anyMatch(it -> Objects.equals(it.getCode(), status.getCode()));
    }

    public static boolean isModelDoesNotHave(XmlModel model, XmlStatus status) {
        return !isModelHave(model, status);
    }

    public static boolean isModelHave(XmlModel model, XmlStatusTo statusTo) {
        XmlStatus status = statusTo.getStatus();
        XmlGroup group = status.getGroup();
        String className = group.getModelClass().getName();
        String groupCode = group.getCode();
        String statusCode = status.getCode();

        XmlModelClass modelClass = model.getClass(className);
        XmlGroup xmlGroup = modelClass.getStatuses().getGroups().stream()
            .filter(it -> Objects.equals(it.getCode(), groupCode))
            .findFirst()
            .orElseThrow();
        XmlStatus xmlStatus = xmlGroup.getStatuses().stream()
            .filter(it -> Objects.equals(it.getCode(), statusCode))
            .findFirst()
            .orElseThrow();
        return xmlStatus.getStatusTos().stream()
            .anyMatch(it -> Objects.equals(it.getStatusTo(), statusTo.getStatusTo()));
    }

    public static boolean isModelDoesNotHave(XmlModel model, XmlStatusTo statusTo) {
        return !isModelHave(model, statusTo);
    }

    public static void addTo(XmlModel model, XmlGroup group) {
        String className = group.getModelClass().getName();

        XmlModelClass modelClass = model.getClass(className);
        if (Objects.isNull(modelClass.getStatuses())) {
            modelClass.setStatuses(new XmlStatuses());
        }
        XmlGroup newGroup = new XmlGroup(group.getCode(), group.getReasonLength());
        newGroup.setModelClass(modelClass);
        modelClass.getStatuses().getGroups().add(newGroup);

        group.getStatuses()
            .forEach(status -> {
                XmlStatus newStatus = new XmlStatus(status.getCode(), status.isInitial());
                newStatus.setGroup(newGroup);
                newGroup.getStatuses().add(newStatus);
                status.getStatusTos()
                    .forEach(to -> {
                        XmlStatusTo newStatusTo = new XmlStatusTo(to.getStatusTo(), to.getLabel());
                        newStatusTo.setStatus(newStatus);
                        newStatus.getStatusTos().add(newStatusTo);
                    });
            });
    }

    public static void addTo(XmlModel model, XmlStatus status) {
        XmlGroup group = status.getGroup();
        String className = group.getModelClass().getName();
        String groupCode = group.getCode();

        XmlModelClass modelClass = model.getClass(className);
        XmlGroup targetGroup = modelClass.getStatuses().getGroups().stream()
            .filter(it -> Objects.equals(it.getCode(), groupCode))
            .findFirst()
            .orElseThrow();

        XmlStatus newStatus = new XmlStatus(status.getCode(), status.isInitial());
        newStatus.setGroup(targetGroup);
        targetGroup.getStatuses().add(newStatus);
        status.getStatusTos()
            .forEach(to -> {
                XmlStatusTo newStatusTo = new XmlStatusTo(to.getStatusTo(), to.getLabel());
                newStatusTo.setStatus(newStatus);
                newStatus.getStatusTos().add(newStatusTo);
            });
    }

    public static void addTo(XmlModel model, XmlStatusTo statusTo) {
        XmlStatus status = statusTo.getStatus();
        XmlGroup group = status.getGroup();
        String className = group.getModelClass().getName();
        String groupCode = group.getCode();
        String statusCode = status.getCode();

        XmlModelClass modelClass = model.getClass(className);
        XmlGroup targetGroup = modelClass.getStatuses().getGroups().stream()
            .filter(it -> Objects.equals(it.getCode(), groupCode))
            .findFirst()
            .orElseThrow();

        XmlStatus targetStatus = targetGroup.getStatuses().stream()
            .filter(it -> Objects.equals(it.getCode(), statusCode))
            .findFirst()
            .orElseThrow();

        XmlStatusTo newStatusTo = new XmlStatusTo(statusTo.getStatusTo(), statusTo.getLabel());
        newStatusTo.setStatus(targetStatus);
        targetStatus.getStatusTos().add(newStatusTo);
    }

    public static boolean isModelHaveStatuses(XmlModel model) {
        return model.getClassesAsList().stream()
            .filter(XmlModelClass::haveStatuses)
            .anyMatch(modelClass -> modelClass.getStatuses().getGroups().stream()
                .anyMatch(group -> !group.getStatuses().isEmpty()));
    }
}
