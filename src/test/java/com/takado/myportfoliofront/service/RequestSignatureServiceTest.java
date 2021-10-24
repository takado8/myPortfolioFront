package com.takado.myportfoliofront.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RequestSignatureServiceTest {
    @Autowired
    RequestSignatureService signatureService;

    @Test
    void testStringToBytesAndReverse() throws GeneralSecurityException {
        byte[] bytes = "hello".getBytes(StandardCharsets.UTF_8);
        System.out.println("\n\nbytes:\n" + Arrays.toString(bytes) + "\n\n");
        String string = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("\n\nstring:\n" + string + "\n\n");
        assertEquals("hello", string);

        java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
        java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();

        String message = "someMessage";
        var signature = signatureService.generateSignature(message);

        String humanReadableString = encoder.encodeToString(signature);
//        String humanReadableString = new String(base64ByteArray, StandardCharsets.UTF_8);
        System.out.println("\n\nsign:\n" + humanReadableString + "\n\n");

        byte[] signatureFromString = decoder.decode(humanReadableString);
        String humanReadableString2 = encoder.encodeToString(signatureFromString);

        System.out.println("\n\nsign2:\n" + humanReadableString2 + "\n\n");

        assertEquals(humanReadableString, humanReadableString2);
        assertEquals(signature, signatureFromString);
    }

    @Test
    void testVerifyDigitalSignature() throws GeneralSecurityException {
        //given
        String message = "someMessage";
        var signature = signatureService.generateSignature(message);
        //when
        var result = signatureService.verifyDigitalSignature(signature, message);
        //then
        assertTrue(result);
    }

    @Test
    void testGenerateSignature() throws GeneralSecurityException {
        var signature = signatureService.generateSignature("someString");
        assertNotNull(signature);
        assertTrue(signature.length > 0);
    }

    @Test
    void generateKeys() throws GeneralSecurityException {
        var keys = signatureService.generateKeyPair();
        var encodedPrivateKey = signatureService.savePrivateKey(keys.getPrivate());
        var encodedPublicKey = signatureService.savePublicKey(keys.getPublic());
        System.out.println("\n\nprivate:");
        System.out.println(encodedPrivateKey);
        System.out.println("public:");
        System.out.println(encodedPublicKey);
        System.out.println("\n\n");
    }

    @Test
    void testSaveAndRetrievePrivateKey() throws GeneralSecurityException {
        //given
        KeyPair keys = signatureService.generateKeyPair();
        PrivateKey privateKey = keys.getPrivate();
        //when
        String savedKey = signatureService.savePrivateKey(privateKey);
        PrivateKey restoredKey = signatureService.loadPrivateKey(savedKey);
        //then
        assertEquals(privateKey, restoredKey);
        assertEquals(signatureService.savePrivateKey(privateKey), signatureService.savePrivateKey(restoredKey));
    }

    @Test
    void testSaveAndRetrievePublicKey() throws GeneralSecurityException {
        //given
        KeyPair keys = signatureService.generateKeyPair();
        PublicKey publicKey = keys.getPublic();
        //when
        String savedKey = signatureService.savePublicKey(publicKey);
        PublicKey restoredKey = signatureService.loadPublicKey(savedKey);
        //then
        assertEquals(publicKey, restoredKey);
        assertEquals(signatureService.savePublicKey(publicKey), signatureService.savePublicKey(restoredKey));
    }
}