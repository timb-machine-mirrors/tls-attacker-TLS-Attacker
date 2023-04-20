/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.constants;

/** */
public enum CertificateKeyType {
    DH,
    ECDH,
    RSA,
    DSS,
    ECDSA,
    GOST01,
    GOST12,
    FORTEZZA,
    ECNRA,
    SM2,
    NONE;
}
