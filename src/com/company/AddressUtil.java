package com.company;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class AddressUtil {

    private static HashMap<String, Address> addressHashMap = new HashMap<>();
    private Gson gson = new Gson();

    public void addAddress(String json){
        int mapSize = addressHashMap.size();
        Map<String, Address> map = gson.fromJson(json, new TypeToken<Map<String, Address>>(){}.getType());
        addressHashMap.putAll(map);

        if (mapSize < addressHashMap.size()) {
            Main.getAndPostAll(Main.port, new ConcurrentHashMap<>(addressHashMap));
        }
    }

    public void readFile() throws FileNotFoundException {
        File file =
                new File("/home/martin/IdeaProjects/project/CryptoCoin/blocks.json");
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine())
            addAddress(sc.nextLine());
    }

    public void addBlock(Address address){

        addressHashMap.put(address.getPublicKey(), address);
    }

    public String getAddress(){
        return gson.toJson(addressHashMap);
    }

    public HashMap<String, Address> getAddressMap() {
        return addressHashMap;
    }
}
