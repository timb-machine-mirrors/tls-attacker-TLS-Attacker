/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.protocol.handler.extension;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.RunningModeType;
import de.rub.nds.tlsattacker.core.exceptions.AdjustmentException;
import de.rub.nds.tlsattacker.core.protocol.message.extension.RecordSizeLimitExtensionMessage;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.transport.ConnectionEndType;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class RecordSizeLimitExtensionHandlerTest {

    private RecordSizeLimitExtensionHandler handler;

    private TlsContext context;

    @Before
    public void setUp() {
        Config config = Config.createConfig();
        config.setDefaultRunningMode(RunningModeType.SERVER);
        context = new TlsContext(config);
        handler = new RecordSizeLimitExtensionHandler(context);
    }

    /**
     * Test of adjustContext method, of class RecordSizeLimitExtensionHandler.
     */
    @Test
    public void testadjustContextConnectionPeer() {
        context.setTalkingConnectionEndType(ConnectionEndType.CLIENT);

        RecordSizeLimitExtensionMessage msg = new RecordSizeLimitExtensionMessage();
        msg.setRecordSizeLimit(new byte[] { (byte) 0x05, (byte) 0x39 });
        assertNull(context.getOutboundRecordSizeLimit());
        handler.adjustContext(msg);
        assertTrue(context.getOutboundRecordSizeLimit() == 1337);
    }

    @Test(expected = AdjustmentException.class)
    public void testadjustContextInvalidSize() {
        RecordSizeLimitExtensionMessage msg = new RecordSizeLimitExtensionMessage();
        msg.setRecordSizeLimit(new byte[] { (byte) 0x05, (byte) 0x39, (byte) 0x00 });
        assertNull(context.getOutboundRecordSizeLimit());
        handler.adjustContext(msg);
    }

    public void testadjustContextSizeTooSmall() {
        RecordSizeLimitExtensionMessage msg = new RecordSizeLimitExtensionMessage();
        msg.setRecordSizeLimit(new byte[] { (byte) 0x00, (byte) 0x2A });
        assertNull(context.getOutboundRecordSizeLimit());
        handler.adjustContext(msg);
        assertNull(context.getOutboundRecordSizeLimit());
    }
}
