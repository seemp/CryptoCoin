package com.company;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Util {

    private static HashMap<Long, Block> blockMap = new HashMap<>();
    private Gson gson = new Gson();

    public void addBlocks(String json){
        int mapSize = blockMap.size();
        Map<Long,Block> map=gson.fromJson(json, new TypeToken<Map<Long,Block>>(){}.getType());
        blockMap.putAll(map);
        if (mapSize < blockMap.size()) {
            Main.getAndPostAll(Main.port, new ConcurrentHashMap<>(blockMap));
        }
    }

    public void readFile() throws FileNotFoundException {
        File file =
                new File("/home/martin/IdeaProjects/project/CryptoCoin/blocks.json");
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine())
            addBlocks(sc.nextLine());
    }

    public void addBlock(Block block){
        blockMap.put(block.getToken(), block);
    }

    public String getBlocks(){
        return gson.toJson(blockMap);
    }

    public HashMap<Long, Block> getBlockMap() {
        return blockMap;
    }
}
