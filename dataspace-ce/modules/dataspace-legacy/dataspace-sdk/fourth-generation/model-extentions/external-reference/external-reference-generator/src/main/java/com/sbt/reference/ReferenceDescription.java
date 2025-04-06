package com.sbt.reference;

import com.sbt.mg.data.model.XmlModelClass;

public class ReferenceDescription {
// reference class name
    private String referenceClassName;
// external object
    private String referenceType;
// name of the collection entity
    private String collectionClassName;
// object from model
    private XmlModelClass entityClass;
// object from the model that is not the root of the aggregate
    private XmlModelClass rootEntityClass;
// class that has the reference declared
    private XmlModelClass classOwner;
//Original type name from Model.xml
    private String originalType;

    public String getReferenceClassName() {
        return referenceClassName;
    }

    public void setReferenceClassName(String referenceClassName) {
        this.referenceClassName = referenceClassName;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public XmlModelClass getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(XmlModelClass entityClass) {
        this.entityClass = entityClass;
    }

    public XmlModelClass getRootEntityClass() {
        return rootEntityClass;
    }

    public void setRootEntityClass(XmlModelClass rootEntityClass) {
        this.rootEntityClass = rootEntityClass;
    }

    public void setCollectionClassName(String collectionClassName) {
        this.collectionClassName = collectionClassName;
    }

    public String getCollectionClassName() {
        return collectionClassName;
    }

    public XmlModelClass getClassOwner() {
        return classOwner;
    }

    public void setClassOwner(XmlModelClass classOwner) {
        this.classOwner = classOwner;
    }

    public String getOriginalType() {
        return originalType;
    }

    public void setOriginalType(String originalType) {
        this.originalType = originalType;
    }
}
