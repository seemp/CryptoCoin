package com.company;

import com.google.gson.Gson;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList<Transaction> transactions = new ArrayList<>();
    public long timeStamp;
    public int nonce;

    public Block(String previousHash ) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.hash = calculateHash();
    }

    public String calculateHash() {
        return StringUtil.applySha256(
                previousHash +
                        timeStamp +
                        nonce +
                        merkleRoot
        );
    }

    public void mineBlock(int difficulty) {
        merkleRoot = StringUtil.getMerkleRoot(transactions);
        String target = StringUtil.getDifficultyString(difficulty); //Create a string with difficulty * "0"
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }

    public boolean addTransaction(Transaction transaction) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        if(transaction == null) return false;
        if((!"0".equals(previousHash))) {
            if((!transaction.processTransaction())) {
                System.out.println("Transaction failed to process. Discarded.");
                return false;
            }
        }

        transactions.add(transaction);
        System.out.println("Transaction Successfully added to Block");
        return true;
    }

    public void addTransactionJSON(String json) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
        Gson gson = new Gson();
        transactions.add(gson.fromJson(json, Transaction.class));
        System.out.println("Transaction Successfully added to Block");

        Transaction transaction = transactions.get(transactions.size() - 1);
        System.out.println(transaction.value + "IGOT IT ");
        if (Main.walletA.publicKey.equals(StringUtil.getKeyFromString(transaction.recipient))) {
            Main.walletA.UTXOs.put(transaction.value, transaction);
            System.out.println(Main.walletA.getBalance());
        }
    }
}