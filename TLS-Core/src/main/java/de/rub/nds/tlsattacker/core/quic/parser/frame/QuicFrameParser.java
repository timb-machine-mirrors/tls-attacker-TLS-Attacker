/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, Technology Innovation Institute, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.quic.parser.frame;

import de.rub.nds.tlsattacker.core.layer.data.Parser;
import de.rub.nds.tlsattacker.core.quic.frame.QuicFrame;
import java.io.InputStream;

public abstract class QuicFrameParser<T extends QuicFrame<T>> extends Parser<T> {

    public QuicFrameParser(InputStream stream) {
        super(stream);
    }

    public void parseFrameType(QuicFrame<T> frame) {
        frame.setFrameType((byte) parseVariableLengthInteger());
    }
}