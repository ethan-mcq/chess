package service;

import dataaccess.Data;

/**
 * Base service class providing common functionality for all service classes.
 */
public class BaseS {
    protected final Data dataAccess;

    /**
     * Constructs a new base service with the given Data access object.
     *
     * @param dataAccess The Data access object
     */
    public BaseS(Data dataAccess) {
        this.dataAccess = dataAccess;
    }
}