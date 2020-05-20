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

    public float getBalance() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        float total = 0;
        for (Map.Entry<Float, Transaction> item: UTXOs.entrySet()){
            Transaction UTXO = item.getValue();
            if(isMine(UTXO.recipient)) { //if output belongs to me ( if coins belong to me )
                UTXOs.put(UTXO.value,UTXO); //add it to our list of unspent transactions.
                total += UTXO.value ;
            }
        }
        return total;
    }

    public boolean isMine(String publicKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        return (StringUtil.getKeyFromString(publicKey) == this.publicKey);
    }


    public Transaction sendFunds(PublicKey _recipient,float value ) {


        float total = 0;

        Transaction newTransaction = new Transaction(publicKey, _recipient , value);
        newTransaction.generateSignature(privateKey);

        return newTransaction;
    }

}
