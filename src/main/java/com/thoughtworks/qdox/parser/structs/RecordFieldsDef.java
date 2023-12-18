package com.thoughtworks.qdox.parser.structs;

import java.util.List;
import java.util.LinkedList;

public class RecordFieldsDef {
    private List<FieldDef> fields = new LinkedList<FieldDef>();

    public void addField(FieldDef field) {
        fields.add(field);
    }

    public List<FieldDef> getFields() {
        return fields;
    }
}
