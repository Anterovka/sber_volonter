package com.sbt.aggregator;

import com.sbt.mg.data.model.XmlModelClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AggregateTree {

    private List<AggregateTree> subAggregates = new ArrayList<>();
    private XmlModelClass xmlModelClassValue;
    private AggregateTree parent;

    public AggregateTree(XmlModelClass xmlModelClassValue) {
        this.xmlModelClassValue = xmlModelClassValue;
    }

    public void addSubAggregate(AggregateTree aggregateTree) {
        aggregateTree.setParent(this);
        subAggregates.add(aggregateTree);
    }

    public List<AggregateTree> getSubAggregates() {
        return subAggregates;
    }

    public XmlModelClass getXmlModelClassValue() {
        return xmlModelClassValue;
    }

    public AggregateTree getParent() {
        return parent;
    }

    public void setParent(AggregateTree aggregateTree) {
        this.parent = aggregateTree;

    }

    public static XmlModelClass getRootAggregate(AggregateTree aggregateTree) {
        AggregateTree parent = aggregateTree.getParent();
        if (parent == null) {
            return aggregateTree.getXmlModelClassValue();
        }

        return getRootAggregate(parent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AggregateTree that = (AggregateTree) o;
        return Objects.equals(xmlModelClassValue, that.xmlModelClassValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(xmlModelClassValue);
    }

    @Override
    public String toString() {
        if (subAggregates.isEmpty()) {
            return xmlModelClassValue.getName();
        }
        String s = subAggregates.toString().replaceAll("\\[], ", "").replaceAll("]", "").replaceAll("\\[", "");

        return s + ", " + xmlModelClassValue.getName();
    }
}


