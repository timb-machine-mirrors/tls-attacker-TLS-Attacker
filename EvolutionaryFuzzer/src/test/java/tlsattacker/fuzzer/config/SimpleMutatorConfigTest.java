/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package tlsattacker.fuzzer.config;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.PojoValidator;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import java.util.logging.Logger;
import tlsattacker.fuzzer.config.mutator.SimpleMutatorConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 
 * @author Robert Merget - robert.merget@rub.de
 */
public class SimpleMutatorConfigTest {

    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
    }

    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     *
     */
    public SimpleMutatorConfigTest() {
    }

    /**
     *
     */
    @Test
    public void testEvolutionaryFuzzerPercentage() {
        SimpleMutatorConfig config = new SimpleMutatorConfig();
        Exception E = null;
        try {
            config.setAddMessagePercentage(101);
        } catch (Exception ex) {
            E = ex;
        }
        assertNotNull("Failure: AddMessagePercentage can be risen above 100%", E);
        E = null;
        try {
            config.setAddRecordPercentage(101);
        } catch (Exception ex) {
            E = ex;
        }
        assertNotNull("Failure: setAddRecordPercentage can be risen above 100%", E);
        E = null;
        try {
            config.setModifyVariablePercentage(101);
        } catch (Exception ex) {
            E = ex;
        }
        assertNotNull("Failure: setModifyVariablePercentage can be risen above 100%", E);
        E = null;
        try {
            config.setRemoveMessagePercentage(101);
        } catch (Exception ex) {
            E = ex;
        }
        assertNotNull("Failure: setRemoveMessagePercentage can be risen above 100%", E);
    }

    private static final Logger LOG = Logger.getLogger(SimpleMutatorConfigTest.class.getName());
}
