package com.company;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    public PrivateKey privateKey;

    public PublicKey publicKey;

    public HashMap<Float,Transaction> UTXOs = new HashMap<>();

    public Wallet() {
        generateKeyPair();
    }


    public void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            keyGen.initialize(ecSpec, random); //256
            KeyPair keyPair = keyGen.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();

        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float getBalance() {
        float total = 0;
        for (Map.Entry<Float, Transaction> item: UTXOs.entrySet()){
            total = total + item.getKey();
        }
        return total;
    }

    public void removeFunds(float amount) {
        float amountLeft = amount;
        if (amount < getBalance()) {
            for (float f:UTXOs.keySet()) {
                if (amountLeft == 0){
                    break;
                }
                if (f < amountLeft) {
                    amountLeft = amountLeft - f;
                    UTXOs.remove(f);
                }
                if (f >= amountLeft) {
                    Transaction temp = UTXOs.get(f);
                    UTXOs.remove(f);
                    f = f - amountLeft;
                    if (f != 0) {
                        UTXOs.put(f, temp);
                    }

                    amountLeft = 0;
                }

            }
        }
    }

    public Transaction sendFunds(PublicKey _recipient,float value ) {


        Transaction newTransaction = new Transaction(publicKey, _recipient , value);
        newTransaction.generateSignature(privateKey);

        return newTransaction;
    }

}
