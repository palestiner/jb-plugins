package dev.palestiner.jbplugins.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nonnull;

import java.util.StringJoiner;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Plugin(
        int id,
        String xmlId,
        String name,
        int downloads,
        double rating,
        boolean hasSource
) {

    @Override
    @Nonnull
    public String toString() {
        return new StringJoiner(", ", "[", "]")
                .add("id=" + id)
                .add("xmlId='" + xmlId + "'")
                .add("name='" + name + "'")
                .add("downloads=" + downloads)
                .add("rating=" + rating)
                .toString();
    }

}
