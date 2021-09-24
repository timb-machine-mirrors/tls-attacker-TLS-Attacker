/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2021 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.workflow.action;

import de.rub.nds.tlsattacker.core.config.Config;
import de.rub.nds.tlsattacker.core.constants.CipherSuite;
import de.rub.nds.tlsattacker.core.exceptions.CryptoException;
import de.rub.nds.tlsattacker.core.record.cipher.RecordBlockCipher;
import de.rub.nds.tlsattacker.core.record.cipher.cryptohelper.KeySetGenerator;
import de.rub.nds.tlsattacker.core.record.layer.TlsRecordLayer;
import de.rub.nds.tlsattacker.core.state.State;
import de.rub.nds.tlsattacker.core.state.TlsContext;
import de.rub.nds.tlsattacker.core.workflow.WorkflowTrace;
import de.rub.nds.tlsattacker.util.tests.SlowTests;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class ChangeMasterSecretActionTest {

    private State state;
    private TlsContext tlsContext;
    private ChangeMasterSecretAction action;

    @Before
    public void setUp() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
        InvalidAlgorithmParameterException, CryptoException {
        Config config = Config.createConfig();
        action = new ChangeMasterSecretAction(new byte[] { 0, 1 });
        WorkflowTrace trace = new WorkflowTrace();
        trace.addTlsAction(action);
        state = new State(config, trace);
        tlsContext = state.getTlsContext();
        tlsContext.setSelectedCipherSuite(CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA);
        tlsContext.setRecordLayer(new TlsRecordLayer(tlsContext));
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setNewValue method, of class ChangeClientRandomAction.
     */
    @Test
    public void testSetNewValue() {
        assertArrayEquals(action.getNewValue(), new byte[] { 0, 1 });
        action.setNewValue(new byte[] { 0 });
        assertArrayEquals(action.getNewValue(), new byte[] { 0 });
    }

    /**
     * Test of getNewValue method, of class ChangeClientRandomAction.
     */
    @Test
    public void testGetNewValue() {
        assertArrayEquals(action.getNewValue(), new byte[] { 0, 1 });
    }

    /**
     * Test of getOldValue method, of class ChangeClientRandomAction.
     */
    @Test
    public void testGetOldValue() {
        tlsContext.setMasterSecret(new byte[] { 3 });
        action.normalize();
        action.execute(state);
        assertArrayEquals(action.getOldValue(), new byte[] { 3 });
    }

    /**
     * Test of execute method, of class ChangeClientRandomAction.
     */
    @Test
    public void testExecute() {
        tlsContext.setMasterSecret(new byte[] { 3 });
        action.execute(state);
        assertArrayEquals(action.getOldValue(), new byte[] { 3 });
        assertArrayEquals(action.getNewValue(), new byte[] { 0, 1 });
        assertArrayEquals(tlsContext.getMasterSecret(), new byte[] { 0, 1 });
        assertTrue(action.isExecuted());

    }

    /**
     * Test of reset method, of class ChangeClientRandomAction.
     */
    @Test
    public void testReset() {
        assertFalse(action.isExecuted());
        action.execute(state);
        assertTrue(action.isExecuted());
        action.reset();
        assertFalse(action.isExecuted());
        action.execute(state);
        assertTrue(action.isExecuted());
    }

    @Test
    @Category(SlowTests.class)
    public void marshalingEmptyActionYieldsMinimalOutput() {
        ActionTestUtils.marshalingEmptyActionYieldsMinimalOutput(ChangeMasterSecretAction.class);
    }

    @Test
    @Category(SlowTests.class)
    public void marshalingAndUnmarshalingEmptyObjectYieldsEqualObject() {
        ActionTestUtils.marshalingAndUnmarshalingEmptyObjectYieldsEqualObject(ChangeMasterSecretAction.class);
    }

    @Test
    @Category(SlowTests.class)
    public void marshalingAndUnmarshalingFilledObjectYieldsEqualObject() {
        ActionTestUtils.marshalingAndUnmarshalingFilledObjectYieldsEqualObject(action);
    }

}
