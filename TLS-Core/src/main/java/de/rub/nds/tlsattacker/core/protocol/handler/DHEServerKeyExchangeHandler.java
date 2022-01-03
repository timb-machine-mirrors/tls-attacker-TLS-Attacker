/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler;

import de.rub.nds.tlsattacker.core.protocol.message.DHEServerKeyExchangeMessage;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import java.math.BigInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DHEServerKeyExchangeHandler<T extends DHEServerKeyExchangeMessage> extends ServerKeyExchangeHandler<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    public DHEServerKeyExchangeHandler(TlsContext tlsContext) {
        super(tlsContext);
    }

    @Override
    public void adjustContext(T message) {
        adjustDhGenerator(message);
        adjustDhModulus(message);
        adjustServerPublicKey(message);
        if (message.getComputations() != null && message.getComputations().getPrivateKey() != null) {
            adjustServerPrivateKey(message);
        }
    }

    private void adjustDhGenerator(T message) {
        tlsContext.setServerDhGenerator(new BigInteger(1, message.getGenerator().getValue()));
        LOGGER.debug("Dh Generator: " + tlsContext.getServerDhGenerator());
    }

    private void adjustDhModulus(T message) {
        tlsContext.setServerDhModulus(new BigInteger(1, message.getModulus().getValue()));
        LOGGER.debug("Dh Modulus: " + tlsContext.getServerDhModulus());
    }

    private void adjustServerPublicKey(T message) {
        tlsContext.setServerDhPublicKey(new BigInteger(1, message.getPublicKey().getValue()));
        LOGGER.debug("Server PublicKey: " + tlsContext.getServerDhPublicKey());
    }

    private void adjustServerPrivateKey(T message) {
        tlsContext.setServerDhPrivateKey(message.getComputations().getPrivateKey().getValue());
        LOGGER.debug("Server PrivateKey: " + tlsContext.getServerDhPrivateKey());
    }
}
