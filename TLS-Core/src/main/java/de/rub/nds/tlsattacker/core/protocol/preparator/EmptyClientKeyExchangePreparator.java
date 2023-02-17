/*
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2023 Ruhr University Bochum, Paderborn University, and Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */
package de.rub.nds.tlsattacker.core.protocol.preparator;

import de.rub.nds.modifiablevariable.util.ArrayConverter;
import de.rub.nds.protocol.constants.NamedEllipticCurveParameters;
import de.rub.nds.protocol.crypto.ec.EllipticCurve;
import de.rub.nds.protocol.crypto.ec.Point;
import de.rub.nds.tlsattacker.core.certificate.CertificateAnalyzer;
import de.rub.nds.tlsattacker.core.constants.NamedGroup;
import de.rub.nds.tlsattacker.core.protocol.message.EmptyClientKeyExchangeMessage;
import de.rub.nds.tlsattacker.core.workflow.chooser.Chooser;
import de.rub.nds.x509attacker.constants.X509PublicKeyType;
import java.math.BigInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.util.BigIntegers;

public class EmptyClientKeyExchangePreparator<T extends EmptyClientKeyExchangeMessage>
        extends ClientKeyExchangePreparator<T> {

    private static final Logger LOGGER = LogManager.getLogger();

    protected byte[] random;
    protected final T msg;
    protected byte[] premasterSecret;

    public EmptyClientKeyExchangePreparator(Chooser chooser, T msg) {
        super(chooser, msg);
        this.msg = msg;
    }

    @Override
    public void prepareHandshakeMessageContents() {
        LOGGER.debug("Preparing EmptyClientKeyExchangeMessage");
        prepareAfterParse(true);
    }

    protected void prepareClientServerRandom(T msg) {
        random = ArrayConverter.concatenate(chooser.getClientRandom(), chooser.getServerRandom());
        msg.getComputations().setClientServerRandom(random);
        random = msg.getComputations().getClientServerRandom().getValue();
        LOGGER.debug(
                "ClientServerRandom: "
                        + ArrayConverter.bytesToHexString(
                                msg.getComputations().getClientServerRandom().getValue()));
    }

    protected byte[] calculateDhPremasterSecret(
            BigInteger modulus, BigInteger privateKey, BigInteger publicKey) {
        if (modulus.compareTo(BigInteger.ZERO) == 0) {
            LOGGER.warn("Modulus is ZERO. Returning empty premaster Secret");
            return new byte[0];
        }
        return BigIntegers.asUnsignedByteArray(publicKey.modPow(privateKey.abs(), modulus.abs()));
    }

    protected void preparePremasterSecret(T msg) {
        msg.getComputations().setPremasterSecret(premasterSecret);
        premasterSecret = msg.getComputations().getPremasterSecret().getValue();
        LOGGER.debug(
                "PremasterSecret: "
                        + ArrayConverter.bytesToHexString(
                                msg.getComputations().getPremasterSecret().getValue()));
    }

    protected byte[] computeECPremasterSecret(
            EllipticCurve curve, Point publicKey, BigInteger privateKey) {
        Point sharedPoint = curve.mult(privateKey, publicKey);
        int elementLength =
                ArrayConverter.bigIntegerToByteArray(sharedPoint.getFieldX().getModulus()).length;
        return ArrayConverter.bigIntegerToNullPaddedByteArray(
                sharedPoint.getFieldX().getData(), elementLength);
    }

    @Override
    public void prepareAfterParse(boolean clientMode) {
        msg.prepareComputations();
        prepareClientServerRandom(msg);

        if (chooser.getContext().getTlsContext().getClientCertificateChain() != null
                && !chooser.getContext()
                        .getTlsContext()
                        .getClientCertificateChain()
                        .getCertificateList()
                        .isEmpty()) {

            X509PublicKeyType certificateKeyType =
                    CertificateAnalyzer.getCertificateKeyType(
                            chooser.getContext()
                                    .getTlsContext()
                                    .getClientCertificateChain()
                                    .getLeaf());

            if (certificateKeyType == X509PublicKeyType.DH) {
                BigInteger modulus =
                        chooser.getContext()
                                .getTlsContext()
                                .getX509Context()
                                .getChooser()
                                .getSubjectDhModulus();

                BigInteger publicKey =
                        chooser.getServerDhPublicKey(); // TODO This should either be the ske key
                // or the certificate public key
                BigInteger privateKey =
                        chooser.getContext()
                                .getTlsContext()
                                .getX509Context()
                                .getChooser()
                                .getSubjectDhPrivateKey();
                premasterSecret = calculateDhPremasterSecret(modulus, privateKey, publicKey);
            } else if (certificateKeyType == X509PublicKeyType.ECDH_ONLY
                    || certificateKeyType == X509PublicKeyType.ECDH_ECDSA
                    || certificateKeyType == X509PublicKeyType.X25519
                    || certificateKeyType == X509PublicKeyType.X448) {
                if (clientMode) {
                    NamedGroup usedGroup = chooser.getSelectedNamedGroup();
                    LOGGER.debug("PMS used Group: " + usedGroup.name());

                    EllipticCurve curve =
                            ((NamedEllipticCurveParameters) usedGroup.getGroupParameters())
                                    .getCurve();
                    Point publicKey = chooser.getServerEcPublicKey();
                    BigInteger privateKey = chooser.getClientEcPrivateKey();
                    premasterSecret = computeECPremasterSecret(curve, publicKey, privateKey);
                } else {
                    LOGGER.warn("KEX with " + certificateKeyType.name() + " not Implemented.");
                }
            }
            preparePremasterSecret(msg);
        }
    }
}
