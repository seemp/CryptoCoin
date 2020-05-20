package com.company;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetAddress;
import java.security.PublicKey;
import java.security.Security;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static ArrayList<Block> lastBlock = new ArrayList<>();

    public static int difficulty = 3;
    public static Wallet walletA;
    public static Transaction genesisTransaction;
    public static AddressUtil addressUtil = new AddressUtil();
    static private Client client = new Client();
    public static int port;
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        walletA = new Wallet();
        port = Integer.parseInt(args[0]);

        httpServer server = new httpServer();

        Thread t1 = new Thread(() -> server.runServer(port));
        t1.start();
        addressUtil.readFile();
        addressUtil.addBlock(new Address(InetAddress.getByName("127.0.0.1"), port, StringUtil.getStringFromKey(walletA.publicKey)));

        Map<String, Address> map = new ConcurrentHashMap<>(addressUtil.getAddressMap());
        System.out.println(addressUtil.getBlocks());
        getAndPostAll(port, map);

        Wallet coinbase = new Wallet();

        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f);
        genesisTransaction.generateSignature(coinbase.privateKey);
        genesisTransaction.transactionId = "0";
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        Block block1 = new Block(genesis.hash);
        lastBlock.add(block1);


        isChainValid();


        while (true) {
            if (lastBlock.get(0).transactions.size() > 2) {
                addBlock(lastBlock.get(0));
            }

            if (scanner.hasNext("mine")) {
                scanner.next();
                lastBlock.get(0).mineBlock(Integer.parseInt(scanner.next()));

            }
            if (scanner.hasNext("transaction")) {
                String current;
                scanner.next();
                current = scanner.next();
                PublicKey pubKey = StringUtil.getKeyFromString(current);
                Transaction t = walletA.sendFunds(pubKey, Float.parseFloat(scanner.next()));
                lastBlock.get(0).addTransaction(t);
                postTransaction(port, new ConcurrentHashMap<>(addressUtil.getAddressMap()), t);

            }
            if (scanner.hasNext("public_key")) {
                System.out.println(StringUtil.getStringFromKey(walletA.publicKey));

            }
            if (scanner.hasNext("balance")) {
                System.out.println(walletA.getBalance());
            }
            if (scanner.hasNext("is_valid")) {
                System.out.println(isChainValid());
            }
            if (scanner.hasNext("block_nr")) {
                System.out.println(blockchain.size());
            }
            if (scanner.hasNext("addresses")) {
                addressUtil.getAddressMap().values().forEach(i -> System.out.println(i.getPort()));
            }
            scanner.next();
        }

    }
    public static Boolean isChainValid() {

        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for(int i = 1; i < blockchain.size(); i++) {

            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);

            if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
                System.out.println("#Current Hashes not equal");
                return false;
            }

            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("#Previous Hashes not equal");
                return false;
            }

            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("#This block hasn't been mined");
                return false;
            }
        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public static Boolean isBlockValid(Block block) {
        Block previous = blockchain.get(blockchain.size() - 1);
        if (block.previousHash.equals(previous.hash)) {
            return true;
        }
        return false;
    }

    public static void addBlockJSON(String newBlock) {
        Gson gson = new Gson();
        Block block = gson.fromJson(newBlock,  Block.class);
        if (isBlockValid(block)) {
            addBlock(block);}
        isChainValid();
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
        lastBlock.remove(newBlock);
        lastBlock.add(new Block(newBlock.hash));

        postBlock(port, new ConcurrentHashMap<>(addressUtil.getAddressMap()),newBlock);

    }

    public static void postBlock(int port, Map<String, Address> map, Block block) {
        map.values().forEach(address -> {
            try {
                if (address.getPort() != port){
                    client.postBlock(address.getIp(), address.getPort(), block);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public static void getAndPostAll(int port, Map<String, Address> map) {
        map.values().forEach(address -> {
            try {
                if (address.getPort() != port){
                    client.postAddress(address.getIp(), address.getPort());
                    client.getAddress(address.getIp(), address.getPort());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void postTransaction(int port, Map<String, Address> map, Transaction transaction) {
        map.values().forEach(address -> {
            try {
                if (address.getPort() != port){
                    client.postTransaction(address.getIp(), address.getPort(), transaction);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
