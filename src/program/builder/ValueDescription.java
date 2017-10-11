/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package program.builder;

import common.VarType;
import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Andrey
 */
public class ValueDescription {
    protected VarType type;

    public String getValue() {
        return value;
    }

    public VarType getType() {
        return type;
    }

    public ValueDescription(VarType type, String value) {
        this.type = type;
        this.value = value;
    }
    protected String value;
    protected ArrayList<Byte> byteValue;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.type);
        hash = 13 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValueDescription other = (ValueDescription) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }
}
