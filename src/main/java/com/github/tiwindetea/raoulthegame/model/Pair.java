//////////////////////////////////////////////////////////////////////////////////
//                                                                              //
//     This Source Code Form is subject to the terms of the Mozilla Public      //
//     License, v. 2.0. If a copy of the MPL was not distributed with this      //
//     file, You can obtain one at http://mozilla.org/MPL/2.0/.                 //
//                                                                              //
//////////////////////////////////////////////////////////////////////////////////

package com.github.tiwindetea.raoulthegame.model;

/**
 * Pair<T extends Object>
 *
 * @param <T> the type parameter
 * @author Lucas LAZARE
 */
public class Pair<T extends Object> {
    private static long currID = Long.MIN_VALUE;
    public static final long ERROR_VAL = Long.MAX_VALUE;
    private long id;
    public T object;

    /**
     * Instanciates a new Pair without consuming an id (the id is set to Pair.ERROR_VAL)
     *
     * @see Pair#ERROR_VAL
     */
    public Pair() {
        this.id = Pair.ERROR_VAL;
        this.object = null;
    }

    /**
     * Instantiates a new Pair with a unique id.
     *
     * @param object the object
     */
    public Pair(T object) {
        if (currID == Long.MAX_VALUE) {
            throw new RuntimeException("Reached max ID");
        }
        this.object = object;
        this.id = currID++;
    }

    /**
     * Gets an unique ID
     *
     * @return an unique ID
     */
    public static long getUniqueId() {
        return currID++;
    }

    /**
     * Generate a pair using the given ID (/!\no checks done on the id)
     *
     * @param id     the id
     * @param object the object
     */
    public Pair(long id, T object) {
        this.id = id;
        this.object = object;
    }

    /**
     * Copy constructor
     *
     * @param pair pair to copy
     */
    public Pair(Pair<? extends T> pair) {
        this.id = pair.id;
        this.object = pair.object;
    }

    /**
     * Copy-caster constructor
     *
     * @param pair   pair to copy
     * @param unused Variable used to avoid type collision with the Pair(Pair<? extends T> pair) constructor
     * @see Pair#Pair(Pair)
     */
    public Pair(Pair<? super T> pair, boolean unused) {
        this.id = pair.id;
        this.object = (T) pair.object;
    }

    /**
     * Instantiates a new Pair. (for research purposes)
     *
     * @param id the id
     */
    public Pair(long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Pair)
            return ((Pair) o).getId() == this.getId();
        else /* Asserting o to be a T */
            return ((T) o).equals(this.object);
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {
        return this.id;
    }
}
