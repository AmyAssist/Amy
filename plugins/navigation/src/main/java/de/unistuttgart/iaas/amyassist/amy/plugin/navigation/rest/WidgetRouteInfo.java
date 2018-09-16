package de.unistuttgart.iaas.amyassist.amy.plugin.navigation.rest;

import java.net.URL;

/**
 *
 * A class for an object that supplies info to the maps widget in the webapp
 *
 * @author Benno Krauß
 */
public class WidgetRouteInfo {
    private URL imageURL;
    private URL link;
    private String linkText;

    public WidgetRouteInfo(URL imageURL, URL link, String linkText) {
        this.imageURL = imageURL;
        this.link = link;
        this.linkText = linkText;
    }

    public URL getImageURL() {
        return imageURL;
    }

    public URL getLink() {
        return link;
    }

    public String getLinkText() {
        return linkText;
    }
}
