package com.takado.myportfoliofront.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RequestSignatureServiceTest {
    @Autowired
    RequestSignatureService signatureService;

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
        var signatureString = Arrays.toString(signature);
        assertNotNull(signatureString);
        assertTrue(signatureString.length() > 0);
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