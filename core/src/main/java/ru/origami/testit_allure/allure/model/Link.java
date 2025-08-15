package ru.origami.testit_allure.allure.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model object that could be used to pass links to external resources to test results.
 *
 * @see ru.origami.testit_allure.allure.model.WithLinks
 * @see ru.origami.testit_allure.allure.model.TestResult
 */
public class Link implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String url;
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
    public Link setName(final String value) {
        this.name = value;
        return this;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param value the value
     * @return self for method chaining
     */
    public Link setUrl(final String value) {
        this.url = value;
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
    public Link setType(final String value) {
        this.type = value;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Link link = (Link) o;
        return Objects.equals(name, link.name)
                && Objects.equals(url, link.url)
                && Objects.equals(type, link.type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, url, type);
    }
}
