/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2022 Ruhr University Bochum, Paderborn University, Hackmanit GmbH
 *
 * Licensed under Apache License, Version 2.0
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package de.rub.nds.tlsattacker.core.certificate.ocsp;

import de.rub.nds.asn1.parser.ParserException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.asn1.x509.Certificate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CertificateInformationExtractorTest {

    /*
     * Test uses a certificate generated by Let's Encrypt. Features must-staple flag for status_request, but not for
     * status_request_v2, because no CA seems to issue such a certificate yet.
     */

    private Certificate certificate;
    private Certificate issuerCertificate;
    private CertificateInformationExtractor certificateInformationExtractor;
    private CertificateInformationExtractor issuerCertificateInformationExtractor;

    @Before
    public void setUp() throws IOException {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("ocsp/muststaple-tlscertchain.bin");
        org.bouncycastle.crypto.tls.Certificate tlsCertificate = org.bouncycastle.crypto.tls.Certificate.parse(stream);

        certificate = tlsCertificate.getCertificateAt(0);
        issuerCertificate = tlsCertificate.getCertificateAt(1);
        certificateInformationExtractor = new CertificateInformationExtractor(certificate);
        issuerCertificateInformationExtractor = new CertificateInformationExtractor(issuerCertificate);
    }

    @Test
    public void testGetMustStaple() throws IOException, ParserException {
        Boolean mustStaple = certificateInformationExtractor.getMustStaple();
        Assert.assertTrue(mustStaple);
    }

    @Test
    public void testGetMustStaplev2() throws IOException, ParserException {
        Boolean mustStaplev2 = certificateInformationExtractor.getMustStaplev2();
        Assert.assertFalse(mustStaplev2);
    }

    @Test
    public void testGetOcspServerUrl() throws NoSuchFieldException, ParserException, IOException {
        String ocspServerUrl = certificateInformationExtractor.getOcspServerUrl();
        Assert.assertEquals("http://ocsp.int-x3.letsencrypt.org", ocspServerUrl);
    }

    @Test
    public void testGetCertificateIssuerUrl() throws NoSuchFieldException, ParserException, IOException {
        String issuerUrl = certificateInformationExtractor.getCertificateIssuerUrl();
        Assert.assertEquals("http://cert.int-x3.letsencrypt.org/", issuerUrl);
    }

    @Test
    public void testGetCertificate() {
        Assert.assertSame(certificateInformationExtractor.getCertificate(), certificate);
    }

    @Test
    public void testGetIssuerNameHash() throws IOException, NoSuchAlgorithmException {
        byte[] expectedNameHash =
            { 126, -26, 106, -25, 114, -102, -77, -4, -8, -94, 32, 100, 108, 22, -95, 45, 96, 113, 8, 93 };
        Assert.assertArrayEquals(expectedNameHash, certificateInformationExtractor.getIssuerNameHash());
    }

    @Test
    public void testGetIssuerKeyHash() throws IOException, NoSuchAlgorithmException {
        byte[] expectedKeyHash =
            { -88, 74, 106, 99, 4, 125, -35, -70, -26, -47, 57, -73, -90, 69, 101, -17, -13, -88, -20, -95 };
        Assert.assertArrayEquals(expectedKeyHash, issuerCertificateInformationExtractor.getIssuerKeyHash());
    }

    @Test
    public void testGetSerialNumber() {
        BigInteger expectedSerialNumber = new BigInteger("403767931667699214058966529413005128395827");
        Assert.assertEquals(expectedSerialNumber, certificateInformationExtractor.getSerialNumber());
    }
}
