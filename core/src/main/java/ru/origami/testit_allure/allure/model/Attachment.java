package ru.origami.testit_allure.allure.model;

import java.io.Serializable;

/**
 * The model object that used to link attachment files, stored in results directory,
 * to test results.
 *
 * @see ru.origami.testit_allure.allure.model.WithAttachments
 */
public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String source;
    private String type;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param value the value
     * @return self for method chaining
     */
    public Attachment setName(final String value) {
        this.name = value;
        return this;
    }

    /**
     * Gets source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets source.
     *
     * @param value the value
     * @return self for method chaining
     */
    public Attachment setSource(final String value) {
        this.source = value;
        return this;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param value the value
     * @return self for method chaining
     */
    public Attachment setType(final String value) {
        this.type = value;
        return this;
    }

}
