package com.sbt.aggregator;

import com.sbt.aggregator.exception.AggregatePositionNotFoundException;
import com.sbt.mg.data.model.XmlModel;
import com.sbt.mg.data.model.XmlModelClass;
import com.sbt.mg.jpa.JpaConstants;
import org.barfuin.texttree.api.DefaultNode;
import org.barfuin.texttree.api.TextTree;
import org.barfuin.texttree.api.TreeOptions;
import org.barfuin.texttree.api.style.TreeStyle;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.sbt.mg.jpa.JpaConstants.ROOT_DICTIONARY_CLASS_NAME;

public class AggregateController {
    private static final Logger LOGGER = Logger.getLogger(AggregateController.class.getName());

    private final Map<XmlModelClass, AggregateTree> positionClassInTree = new HashMap<>();

    public AggregateController() {
    }

    public boolean aggregateTreeContains(XmlModelClass xmlModelClassValue) {
        return positionClassInTree.containsKey(xmlModelClassValue);
    }

    public XmlModelClass controllingClass(XmlModelClass xmlModelClassValue) {
        AggregateTree aggregateTree = classToAggregateTree(xmlModelClassValue);

        return aggregateTree != null ? aggregateTree.getXmlModelClassValue() : null;
    }

    public AggregateTree classToAggregateTree(XmlModelClass xmlModelClassValue) {
        if (aggregateTreeContains(xmlModelClassValue)) {
            return positionClassInTree.get(xmlModelClassValue);
        }

        return null;
    }

    public void setAggregateTreeForClass(XmlModelClass xmlModelClassValue, AggregateTree aggregateTree) {
        positionClassInTree.put(xmlModelClassValue, aggregateTree);
    }

    public XmlModelClass getAggregateParent(XmlModelClass modelClass) {
        if (modelClass.isDictionary()) {
            return modelClass.getModel().getClass(ROOT_DICTIONARY_CLASS_NAME);
        }
        String affinity = modelClass.getAffinity();

        if (affinity == null) {
            return null;
        }

        if (JpaConstants.OBJECT_ID.equals(affinity)) {
            return modelClass;
        } else {
            AggregateTree aggregateTree = positionClassInTree.get(modelClass);
            if (aggregateTree == null) {
                throw new AggregatePositionNotFoundException(modelClass);
            }
            AggregateTree parentTree = aggregateTree.getParent();
            return parentTree == null ? modelClass : parentTree.getXmlModelClassValue();
        }
    }

    public XmlModelClass getRootAggregate(XmlModelClass xmlModelClassValue) {
        AggregateTree aggregateTree = positionClassInTree.get(xmlModelClassValue);
        if (aggregateTree == null) {
            return null;
        }
        return AggregateTree.getRootAggregate(aggregateTree);
    }

    public Collection<XmlModelClass> getAggregatesRoots() {
        return positionClassInTree.entrySet().stream()
            .filter(entry -> Objects.isNull(entry.getValue().getParent()))
            .filter(entry -> !entry.getKey().isAbstract())
            .map(Map.Entry::getKey)
            .toList();
    }

    public AggregateTree addAggregator(XmlModelClass modelClass) {
        AggregateTree aggregateTree = new AggregateTree(modelClass);
        positionClassInTree.put(modelClass, aggregateTree);

        return aggregateTree;
    }

    public void forEach(Consumer<Map.Entry<XmlModelClass, AggregateTree>> entryConsumer) {
        positionClassInTree.entrySet().forEach(entryConsumer);
    }

    public void printAggregates(XmlModel model) {
        Set<AggregateTree> topAggregates = new HashSet<>();

        model.getClassesAsList().forEach(modelClass -> {
            XmlModelClass rootAggregate = getRootAggregate(modelClass);

            if (rootAggregate != null) {
                topAggregates.add(positionClassInTree.get(rootAggregate));
            }
        });

        StringBuilder aggregateStringBuilder = new StringBuilder();
        aggregateStringBuilder.append("\nTable of aggregates\n");
        DefaultNode rootNode = new DefaultNode("Aggregates of model " + model.getModelName());

        topAggregates.forEach(aggregateTree -> aggregateViewFilling(aggregateTree, rootNode));

        TreeOptions options = new TreeOptions();
        options.setStyle(new TreeStyle("+- ", "|  ", "\\- "));
        options.setEnableDefaultColoring(true);

        String rendered = TextTree.newInstance(options).render(rootNode);

        aggregateStringBuilder.append(rendered);

        LOGGER.info(aggregateStringBuilder.toString());
    }

    private void aggregateViewFilling(AggregateTree aggregateTree, DefaultNode rootNode) {
        XmlModelClass classValue = aggregateTree.getXmlModelClassValue();

        if (classValue.isAbstract()) {
            return;
        }

        DefaultNode subNode = new DefaultNode(classValue.getName());
        rootNode.addChild(subNode);

        aggregateTree.getSubAggregates().forEach(aggregateTree1 -> aggregateViewFilling(aggregateTree1, subNode));
    }
}
