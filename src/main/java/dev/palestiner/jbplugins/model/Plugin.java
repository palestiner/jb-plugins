package dev.palestiner.jbplugins.model;

import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.StringJoiner;

public record Plugin(
        int id,
        String xmlId,
        String link,
        String name,
        String preview,
        int downloads,
        String pricingModel,
        String icon,
        String previewImage,
        long cdate,
        double rating,
        boolean hasSource,
        ArrayList<String> tags,
        Vendor vendor
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
