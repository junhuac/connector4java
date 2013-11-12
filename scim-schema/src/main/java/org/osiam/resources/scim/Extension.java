package org.osiam.resources.scim;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.osiam.resources.helper.ExtensionSerializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;

/**
 * This class represents a schema extension as specified by the Scim 2.0 specification.
 */
@JsonSerialize(using = ExtensionSerializer.class)
public class Extension {

    @JsonIgnore
    private String urn;

    private Map<String, Field> fields = new HashMap<>();

    /**
     * Default constructor for Jackson
     */
    protected Extension() {
    }

    /**
     * Constructs an extension with the given urn.
     * 
     * @param urn
     *            the urn of the extension
     */
    public Extension(String urn) {
        this.urn = urn;
    }

    /**
     * Returns the URN of this extension.
     * 
     * @return The URN
     */
    public String getUrn() {
        return urn;
    }

    /**
     * Return the value for the field with a given name and type.
     * 
     * @param field
     *            The name of the field to retrieve the value of.
     * @param extensionFieldType
     *            The type of the field.
     * @return The value for the field with the given name.
     * @throws NoSuchElementException
     *             if this schema does not contain a field of the given name.
     * @throws IllegalArgumentException
     *             if the given field is null or an empty string or if the extensionFieldType is null.
     */
    public <T> T getField(String field, ExtensionFieldType<T> extensionFieldType) {
        if (field == null || field.isEmpty()) {
            throw new IllegalArgumentException("Invalid field name");
        }
        if (extensionFieldType == null) {
            throw new IllegalArgumentException("Invalid field type");
        }

        if (!isFieldPresent(field)) {
            throw new NoSuchElementException("Field " + field + " not valid in this extension");
        }

        return extensionFieldType.fromString(fields.get(field).value);
    }

    /**
     * Update the field with the given name to the given value.
     * 
     * @param field
     *            The name of the field whose value to set
     * @param value
     *            The new value of the field.
     * @throws IllegalArgumentException
     *             if the given field is null or does not exists.
     */
    @Deprecated
    public void setField(String field, String value) {
        if (field == null || !fields.containsKey(field)) {
            throw new IllegalArgumentException("Invalid field name");
        }
        fields.put(field, new Field(ExtensionFieldType.STRING, value));
    }

    /**
     * Adds or updates the field specified by the given field name with the given value.
     * 
     * @param fieldName
     *            the field name
     * @param value
     *            the new value
     */
    public void addOrUpdateField(String fieldName, String value) {
        addOrUpdateField(fieldName, value, ExtensionFieldType.STRING);
    }

    /**
     * Adds or updates the field specified by the given field name with the given value.
     * 
     * @param fieldName
     *            the field name
     * @param value
     *            the new value
     */
    public void addOrUpdateField(String fieldName, Boolean value) {
        addOrUpdateField(fieldName, value, ExtensionFieldType.BOOLEAN);
    }

    /**
     * Adds or updates the field specified by the given field name with the given value.
     * 
     * @param fieldName
     *            the field name
     * @param value
     *            the new value
     */
    public void addOrUpdateField(String fieldName, byte[] value) {
        addOrUpdateField(fieldName, value, ExtensionFieldType.BINARY);
    }

    /**
     * Adds or updates the field specified by the given field name with the given value.
     * 
     * @param fieldName
     *            the field name
     * @param value
     *            the new value
     */
    public void addOrUpdateField(String fieldName, BigInteger value) {
        addOrUpdateField(fieldName, value, ExtensionFieldType.INTEGER);
    }

    /**
     * Adds or updates the field specified by the given field name with the given value.
     * 
     * @param fieldName
     *            the field name
     * @param value
     *            the new value
     */
    public void addOrUpdateField(String fieldName, BigDecimal value) {
        addOrUpdateField(fieldName, value, ExtensionFieldType.DECIMAL);
    }

    /**
     * Adds or updates the field specified by the given field name with the given value.
     * 
     * @param fieldName
     *            the field name
     * @param value
     *            the new value
     */
    public void addOrUpdateField(String fieldName, Date value) {
        addOrUpdateField(fieldName, value, ExtensionFieldType.DATE_TIME);
    }

    /**
     * Adds or updates the field specified by the given field name with the given value.
     * 
     * @param fieldName
     *            the field name
     * @param value
     *            the new value
     */
    public void addOrUpdateField(String fieldName, URI value) {
        addOrUpdateField(fieldName, value, ExtensionFieldType.REFERENCE);
    }

    /**
     * Adds or updates the field specified by the given field name with the given value of the given type.
     * 
     * @param fieldName
     *            the field name
     * @param value
     *            the new value
     * @param type
     *            the scim type of the field
     */
    public <T> void addOrUpdateField(String fieldName, T value, ExtensionFieldType<T> type) {
        if (fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("Invalid field name");
        }
        if (value == null) {
            throw new IllegalArgumentException("Invalid value");
        }
        fields.put(fieldName, new Field(type, type.toString(value)));
    }

    /**
     * Provides a {@link Map} containing the entries of the extension. Note that the returned {@link Map} is immutable.
     * 
     * @return The Entries of this schema as an map.
     */
    @JsonIgnore
    public Map<String, Field> getAllFields() {
        return ImmutableMap.copyOf(fields);
    }

    /**
     * Checks if the given field is present in this extension because not every field is mandatory (according to scim
     * 2.0 spec).
     * 
     * @param field
     *            Name of the field to check
     * @return true if the given field is present, else false
     */
    public boolean isFieldPresent(String field) {
        return fields.containsKey(field);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fields == null) ? 0 : fields.hashCode());
        result = prime * result + ((urn == null) ? 0 : urn.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Extension other = (Extension) obj;
        if (fields == null) {
            if (other.fields != null)
                return false;
        } else if (!fields.equals(other.fields))
            return false;
        if (urn == null) {
            if (other.urn != null)
                return false;
        } else if (!urn.equals(other.urn))
            return false;
        return true;
    }

    /**
     * This class represents a field of an extension with its type and value. Instances of this class are immutable.
     */
    public static final class Field {
        private final ExtensionFieldType<?> type;
        private final String value;

        /**
         * Constructs a new {@link Field} with the given type and value.
         * 
         * @param type
         *            the type of the field
         * @param value
         *            the value of the field
         */
        public Field(ExtensionFieldType<?> type, String value) {
            this.type = type;
            this.value = value;
        }

        /**
         * Returns the type of the {@link Field}
         * 
         * @return the type of the {@link Field}
         */
        public ExtensionFieldType<?> getType() {
            return type;
        }

        /**
         * Returns the value of the {@link Field}
         * 
         * @return the value of the {@link Field}
         */
        public String getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((type == null) ? 0 : type.hashCode());
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
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
            Field other = (Field) obj;
            if (type == null) {
                if (other.type != null) {
                    return false;
                }
            } else if (!type.equals(other.type)) {
                return false;
            }
            if (value == null) {
                if (other.value != null) {
                    return false;
                }
            } else if (!value.equals(other.value)) {
                return false;
            }
            return true;
        }

    }
}
