package com.takado.myportfoliofront.service;

import com.takado.myportfoliofront.domain.DigitalSignature;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;


@Service
public class RequestSignatureService {
    // todo: move keys to application.properties
    private final String privateKeyString = "MIIBSwIBADCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoEFgIUG9lAxlcOP39VvbNVcubhwNxin8s=";
    private final String publicKeyString = "MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAO9nKmRN7dwPRzsv0/UvWjiKKuoc0xvYyFaYoH5Xct0Os2Cz2yJNu6Cdgl0VUGDFX2M7vr3cpyEnDpmU2ssN2cMYkOxcOq/aFNH5M6nmBFA05VLW85XHRTcLxUSbBFvkLjYM6wJW0Jd98IM8WuKNxk3VfeH8XONJXFcNl2DgXQdz";
    Base64.Decoder base64Decoder = Base64.getDecoder();
    Base64.Encoder base64Encoder = Base64.getEncoder();

    public DigitalSignature generateSignature(String message) throws GeneralSecurityException {
        PrivateKey privateKey = loadPrivateKey(privateKeyString);
        Signature signatureService = Signature.getInstance("SHA1withDSA", "SUN");
        signatureService.initSign(privateKey);
        byte[] bytes = message.getBytes();
        signatureService.update(bytes);
        return new DigitalSignature(signatureService.sign(), message);
    }

    public boolean verifyDigitalSignature(DigitalSignature digitalSignature1) throws GeneralSecurityException {
        byte[] signatureBytes = digitalSignature1.getSignature();
        String message = digitalSignature1.getMessage();

        PublicKey publicKey = loadPublicKey(publicKeyString);
        Signature signatureService = Signature.getInstance("SHA1withDSA", "SUN");
        signatureService.initVerify(publicKey);
        byte[] bytes = message.getBytes();
        signatureService.update(bytes);
        return signatureService.verify(signatureBytes);
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        keyGen.initialize(1024, random);
        return keyGen.generateKeyPair();
    }

    public PrivateKey loadPrivateKey(String privateKeyString) throws GeneralSecurityException {
        byte[] clear = base64Decoder.decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("DSA");
        PrivateKey privateKey = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return privateKey;
    }

    public PublicKey loadPublicKey(String publicKeyString) throws GeneralSecurityException {
        byte[] data = base64Decoder.decode(publicKeyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePublic(spec);
    }

    public String savePrivateKey(PrivateKey privateKey) throws GeneralSecurityException {
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        PKCS8EncodedKeySpec spec = keyFactory.getKeySpec(privateKey,
                PKCS8EncodedKeySpec.class);
        byte[] packed = spec.getEncoded();
        String privateKeyString = base64Encoder.encodeToString(packed);
        Arrays.fill(packed, (byte) 0);
        return privateKeyString;
    }

    public String savePublicKey(PublicKey publicKey) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("DSA");
        X509EncodedKeySpec spec = fact.getKeySpec(publicKey,
                X509EncodedKeySpec.class);
        return base64Encoder.encodeToString(spec.getEncoded());
    }
}
