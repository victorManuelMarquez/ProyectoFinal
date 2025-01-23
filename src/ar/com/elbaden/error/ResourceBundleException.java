package ar.com.elbaden.error;

import java.util.MissingResourceException;

public class ResourceBundleException extends Exception {

    public ResourceBundleException(MissingResourceException cause) {
        super(cause);
    }

}
