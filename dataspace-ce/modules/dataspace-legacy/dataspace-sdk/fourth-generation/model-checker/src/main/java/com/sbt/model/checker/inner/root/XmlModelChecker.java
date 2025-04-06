package com.sbt.model.checker.inner.root;

import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.exception.checkmodel.TableNamePrefixException;
import com.sbt.mg.exception.checkmodel.TablePrefixNameChangeException;
import com.sbt.model.exception.UserUnusedSchemaItemsSectionException;

import java.util.Objects;

import static com.sbt.mg.ModelHelper.validateTablePrefix;

public class XmlModelChecker implements RootChecker {

    private final XmlModel prevModel;
    private final XmlModel model;

    public XmlModelChecker(XmlModel prevModel, XmlModel newModel) {
        this.model = newModel;
        this.prevModel = prevModel;
    }

    @Override
    public void check() {
        checkModelDoesNotHaveUnusedSchemaItemsSection();
        initAndCheckTablePrefix(prevModel, this.model);
    }

    private void checkModelDoesNotHaveUnusedSchemaItemsSection() {
        if (Objects.nonNull(this.model.getUnusedSchemaItems())) {
            throw new UserUnusedSchemaItemsSectionException();
        }
    }

    public static void initAndCheckTablePrefix(XmlModel prevModel, XmlModel newModel) {
        if (prevModel != null &&
                !Objects.equals(
                        prevModel.getTablePrefix() == null ? "" : prevModel.getTablePrefix(),
                        newModel.getTablePrefix() == null ? "" : newModel.getTablePrefix())) {
            throw new TablePrefixNameChangeException(prevModel.getTablePrefix(), newModel.getTablePrefix());
        }

        initAndCheckTablePrefix(newModel);
    }

    public static void initAndCheckTablePrefix(XmlModel model) {
        String tablePrefix = model.getTablePrefix();
        if (tablePrefix == null) {
            model.setTablePrefix("");
        } else {
            validateTablePrefix(tablePrefix,
                    new TableNamePrefixException(tablePrefix));

            model.setTablePrefix(tablePrefix);
        }
    }
}
