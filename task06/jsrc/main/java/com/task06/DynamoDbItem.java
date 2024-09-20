package com.task06;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class DynamoDbItem {

    private String id;
    private String itemKey;
    private String modificationTime;
    private String updatedAttribute;
    private String newValue;
    private String oldValue;

    public DynamoDbItem() {
    }

    public DynamoDbItem(String id, String itemKey, String modificationTime, String newValue) {
        this.id = id;
        this.itemKey = itemKey;
        this.modificationTime = modificationTime;
        this.newValue = newValue;
    }

    public DynamoDbItem(String id, String itemKey, String modificationTime, String updatedAttribute, String newValue, String oldValue) {
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

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getUpdatedAttribute() {
        return updatedAttribute;
    }

    public void setUpdatedAttribute(String updatedAttribute) {
        this.updatedAttribute = updatedAttribute;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
}
