package com.company;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public class Transaction {

    public String transactionId;
    public String sender;
    public String recipient;
    public float value;
    public String signature;


    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value) {
        this.sender = StringUtil.getStringFromKey(from);
        this.recipient = StringUtil.getStringFromKey(to);
        this.value = value;
    }

    public boolean processTransaction() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

        if(!verifySignature()) {
            System.out.println("#Transaction Signature failed to verify");
            return false;
        }

        return true;
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = sender + recipient + value;
        byte[] signaturebytes = StringUtil.applyECDSASig(privateKey,data);
        signature = StringUtil.getBase64String(signaturebytes);
    }

    public boolean verifySignature() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        String data = sender + recipient + value;
        byte[] signaturebytes = StringUtil.getBytesFromBase64(signature);
        return StringUtil.verifyECDSASig(StringUtil.getKeyFromString(sender), data, signaturebytes);
    }
}