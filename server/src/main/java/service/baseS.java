package service;

import dataaccess.data;

/**
 * Base service class providing common functionality for all service classes.
 */
public class baseS {
    protected final data dataAccess;

    /**
     * Constructs a new base service with the given data access object.
     *
     * @param dataAccess The data access object
     */
    public baseS(data dataAccess) {
        this.dataAccess = dataAccess;
    }
}