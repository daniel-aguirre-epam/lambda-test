package com.task06;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class DynamoDbItem {

    private String id;
    private String itemKey;
    private String modificationTime;
    private String updatedAttribute;
    private Object newValue;
    private Object oldValue;

    public DynamoDbItem() {
    }

    public DynamoDbItem(String id, String itemKey, String modificationTime, Object newValue) {
        this.id = id;
        this.itemKey = itemKey;
        this.modificationTime = modificationTime;
        this.newValue = newValue;
    }

    public DynamoDbItem(String id, String itemKey, String modificationTime, String updatedAttribute, String newValue, Object oldValue) {
        this.id = id;
        this.itemKey = itemKey;
        this.modificationTime = modificationTime;
        this.updatedAttribute = updatedAttribute;
        this.newValue = newValue;
        this.oldValue = oldValue;
    }

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(String modificationTime) {
        this.modificationTime = modificationTime;
    }

    @DynamoDbConvertedBy(ObjectAttributeConverter.class)
    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(Object newValue) {
        this.newValue = newValue;
    }

    public String getUpdatedAttribute() {
        return updatedAttribute;
    }

    public void setUpdatedAttribute(String updatedAttribute) {
        this.updatedAttribute = updatedAttribute;
    }

    @DynamoDbConvertedBy(ObjectAttributeConverter.class)
    public Object getOldValue() {
        return oldValue;
    }

    public void setOldValue(Object oldValue) {
        this.oldValue = oldValue;
    }
}
