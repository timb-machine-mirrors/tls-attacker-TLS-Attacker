/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.layer.data;

import de.rub.nds.tlsattacker.core.exceptions.PreparationException;
import de.rub.nds.tlsattacker.core.workflow.chooser.Chooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @param <T> The Object that should be prepared
 */
public abstract class Preparator<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    protected final Chooser chooser;
    private final T object;

    public Preparator(Chooser chooser, T object) {
        this.chooser = chooser;
        this.object = object;
        if (object == null) {
            throw new PreparationException("Cannot prepare NULL");
        }
    }

    public abstract void prepare();

    public void prepareAfterParse() {}

    public T getObject() {
        return object;
    }

    public void afterPrepare() {}
}
