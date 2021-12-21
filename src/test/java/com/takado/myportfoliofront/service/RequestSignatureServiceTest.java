package com.takado.myportfoliofront.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RequestSignatureServiceTest {
    @Autowired
    RequestSignatureService signatureService;

    @Test
    void verifyDigitalSignature() throws GeneralSecurityException {
        //given
        String message = "someMessage";
        var signature = signatureService.generateSignature(message);
        //when
        var result = signatureService.verifyDigitalSignature(signature);
        //then
        assertTrue(result);
    }

    @Test
    void generateSignature() throws GeneralSecurityException {
        //when
        var signature = signatureService.generateSignature("someString");
        //then
        assertNotNull(signature);
        assertNotNull(signature.getSignature());
        assertTrue(signature.getSignature().length > 0);
    }

    @Test
    void saveAndRetrievePrivateKey() throws GeneralSecurityException {
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
    void saveAndRetrievePublicKey() throws GeneralSecurityException {
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

    @Test
    void generateKeys() throws GeneralSecurityException {
        //when
        var keys = signatureService.generateKeyPair();
        var encodedPrivateKey = signatureService.savePrivateKey(keys.getPrivate());
        var encodedPublicKey = signatureService.savePublicKey(keys.getPublic());
        //then
        assertNotNull(encodedPrivateKey);
        assertNotNull(encodedPublicKey);
        System.out.println("\n\nprivate:");
        System.out.println(encodedPrivateKey);
        System.out.println("public:");
        System.out.println(encodedPublicKey);
        System.out.println("\n\n");
    }
}