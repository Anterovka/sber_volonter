package com.sbt.mg;

import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.jpa.JpaConstants;
import com.sbt.parameters.enums.Changeable;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;

import java.util.List;
import java.util.stream.Collectors;

import static com.sbt.mg.utils.ClassUtils.isBaseClass;

/**
 * Class with static methods that builds trees from some input data
 */
public class ModelInverterHelper {

    /**
     * Method builds a tree from the model after pre-filtering it
     *
     * @param xmlModel model from which classes are retrieved
     * @return model in the form of a tree
     */
    public static TreeNode<String> invert(XmlModel xmlModel) {
        return invert(xmlModel.getClassesAsList().stream()
            .filter(modelClass -> modelClass.getClassAccess() != Changeable.SYSTEM)
            .collect(Collectors.toList()));
    }

    /**
     * Method builds model tree from class list. Root element of tree is node where empty string is stored.
     * Each subtree is a class of the model.
     *
     * @param classes from which we read the model in the form of a list
     * @return model in the form of a tree
     */
    public static TreeNode<String> invert(List<XmlModelClass> classes) {
        TreeNode<String> base = new ArrayMultiTreeNode<>("");

        for (XmlModelClass modelClass : classes) {
            addClassAndAllOfHisParentsToTree(classes, modelClass, base);
        }
        return base;
    }

    private static TreeNode<String> addClassAndAllOfHisParentsToTree(List<XmlModelClass> classes,
                                                                     XmlModelClass modelClass,
                                                                     TreeNode<String> baseNode) {
        String extendType = modelClass.getExtendedClassName();
        TreeNode<String> foundNode = baseNode.find(modelClass.getName());
        if (foundNode != null) {
            return foundNode;
        }
        TreeNode<String> newNode = new ArrayMultiTreeNode<>(modelClass.getName());
        if (extendType == null ||
            isBaseClass(extendType) ||
            extendType.equals(JpaConstants.BASE_EVENT_NAME) ||
            extendType.equals(JpaConstants.BASE_MERGE_EVENT_NAME)) {
            baseNode.add(newNode);
        } else {
            TreeNode<String> parentNode = addClassAndAllOfHisParentsToTree(classes, findClass(classes, extendType), baseNode);
            parentNode.add(newNode);
        }
        return newNode;
    }

    /**
     * Finds a class with a given name in the collection
     */
    private static XmlModelClass findClass(List<XmlModelClass> classes, String className) {
        for (XmlModelClass xmlModelClass : classes) {
            if (xmlModelClass.getName().equals(className)) {
                return xmlModelClass;
            }
        }
        return null;
    }

    private static TreeNode<String> copyTreeNode(TreeNode<String> nodeToCopy) {
        ArrayMultiTreeNode<String> newNode = new ArrayMultiTreeNode<>(nodeToCopy.data());
        if (!nodeToCopy.isLeaf()) {
            for (TreeNode<String> subtree : nodeToCopy.subtrees()) {
                newNode.add(copyTreeNode(subtree));
            }
        }
        return newNode;
    }
}

