/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.quic.serializer.frame;

import de.rub.nds.tlsattacker.core.quic.frame.MaxStreamsFrame;
import de.rub.nds.tlsattacker.core.quic.util.VariableLengthIntegerEncoding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaxStreamsFrameSerializer extends QuicFrameSerializer<MaxStreamsFrame> {

    private static final Logger LOGGER = LogManager.getLogger();

    public MaxStreamsFrameSerializer(MaxStreamsFrame frame) {
        super(frame);
    }

    @Override
    protected byte[] serializeBytes() {
        writeFrameType();
        writeMaximumStreams();
        return getAlreadySerialized();
    }

    private void writeMaximumStreams() {
        appendBytes(
                VariableLengthIntegerEncoding.encodeVariableLengthInteger(
                        frame.getMaximumStreams().getValue()));
        LOGGER.debug("Maximum Streams: {}", frame.getMaximumStreams().getValue());
    }
}
