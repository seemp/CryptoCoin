package com.company;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.InetAddress;
import java.security.PublicKey;
import java.security.Security;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static HashMap<Long, Block> blockHashMap = new HashMap<>();
    public static ArrayList<Block> lastBlock = new ArrayList<>();
    public static Block genesis = new Block("0", 0L);
    public static int difficulty = 4;
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
        System.out.println(addressUtil.getAddress());
        getAndPostAll(port, map);
        if (port == 1500) {
            Wallet wallet1 = new Wallet();

            genesisTransaction = new Transaction(wallet1.publicKey, walletA.publicKey, 10000f);
            genesisTransaction.generateSignature(wallet1.privateKey);
            genesisTransaction.transactionId = "0";
            Block genesis = new Block("0", 0L);
            genesis.addTransaction(genesisTransaction);
            addBlock(genesis);

            Block block1 = new Block(genesis.hash, 1);
            lastBlock.add(block1);
        }else {
            Block block1 = new Block(genesis.hash, 1);
            lastBlock.add(block1);
            getAllBlocks(port, map);
        }

        isChainValid();


        while (true) {
            if (lastBlock.size() > 0) {
                if (lastBlock.get(0).transactions.size() > 2) {
                    addBlock(lastBlock.get(0));
                }
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
                if (lastBlock.get(0).addTransaction(t)){
                postTransaction(port, new ConcurrentHashMap<>(addressUtil.getAddressMap()), t);}

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
                addressUtil.getAddressMap().values().forEach(i -> System.out.println(i.toString()));
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

    public static void removeSplits() {
        for (Block b:blockchain
             ) {
            putToMap(b.nr, b);
        }
        blockHashMap.clear();
    }

    public static void putToMap(long key, Block block) {
        Block currentblock = blockHashMap.get(key);

            if (blockHashMap.containsKey(key)) {

                if (block.transactions.size() > currentblock.transactions.size()) {
                    replaceElement(key, block);
                } else if (block.transactions.size() == currentblock.transactions.size()) {
                    if (block.timeStamp < currentblock.timeStamp) {
                        replaceElement(key, block);
                    } else if (block.timeStamp == currentblock.timeStamp) {
                        if (block.hash.compareTo(currentblock.hash) > 0) {
                            replaceElement(key, block);
                        }
                    }
                }

            } else {
                blockHashMap.put(key, block);
            }

    }

    private static void replaceElement(long key, Block block) {
        blockchain.remove(blockHashMap.get(key));
        blockHashMap.remove(key);
        blockHashMap.put(key, block);
        blockchain.add(block);
    }

    public static Boolean isBlockValid(Block block) {
        if (blockchain.size() > 1) {
        Block previous = blockchain.get(blockchain.size() - 1);
        return block.previousHash.equals(previous.hash) && block.nr == previous.nr -1;
        } else {
            return block.previousHash.equals(genesis.hash) && block.nr == genesis.nr + 1;
        }

    }

    public static void addBlockJSON(String newBlock) {
        Gson gson = new Gson();
        Block block = gson.fromJson(newBlock,  Block.class);
        if (isBlockValid(block)) {
            addBlock(block);
        }
        removeSplits();
        isChainValid();
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
        lastBlock.remove(newBlock);
        lastBlock.add(new Block(newBlock.hash, newBlock.nr));

        postBlock(port, new ConcurrentHashMap<>(addressUtil.getAddressMap()),newBlock);

    }

    public static void addBlocksJSON(String newChain) {
        Gson gson = new Gson();
        blockchain = gson.fromJson(newChain, new TypeToken<List<Block>>(){}.getType());

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

    public static void postBlocks(int port, Map<String, Address> map, ArrayList<Block> blockchain) {
        map.values().forEach(address -> {
            try {
                if (address.getPort() != port){
                    client.postBlocks(address.getIp(), address.getPort(), blockchain);
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

    public static void getAllBlocks(int port, Map<String, Address> map) {
        map.values().forEach(address -> {
            try {
                if (address.getPort() != port){
                    client.getBlocks(address.getIp(), address.getPort());

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Block last = blockchain.get(blockchain.size()-1);
            lastBlock.add(new Block(last.hash,last.nr));
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
