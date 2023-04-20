package com.tintin3274.wordbansay.controller;

import com.tintin3274.wordbansay.model.Player;
import com.tintin3274.wordbansay.model.UpdateScore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;

@RestController
public class MainController {
    private static boolean initialize = false;
    private static boolean playing = false;
    private static int playerCount = 0;
    private static int round = 0;
    private static Map<String, Player> playerMap = new HashMap<>();
    private static List<Player> playerList = new ArrayList<>();
    private static List<String> words;
    private static final String FILE_NAME = "words.txt";

    @GetMapping("/register/{name}")
    public String register(@PathVariable String name) {
        if (initialize && !playing) {
            String uuid = String.valueOf(UUID.randomUUID());
            Player player = new Player();
            player.setUuid(uuid);
            player.setId(++playerCount);
            player.setName(name);
            playerMap.put(uuid, player);
            playerList.add(player);
            return "Go to path: /player/"+uuid;
        }
        return "Now Playing!, Can't create new user.";
    }

    @GetMapping("/initialize")
    public String initialize() {
        if (!initialize) {
            try {
                words = new ArrayList<>();
                Resource resource = new ClassPathResource(FILE_NAME);
                FileInputStream file = new FileInputStream(resource.getFile());
                BufferedReader reader = new BufferedReader(new InputStreamReader(file));
                String currentLine;
                while ((currentLine = reader.readLine()) != null) {
                    words.add(currentLine);
                }
                reader.close();
                initialize = true;
            } catch (IOException e) {
                return "Initialize Game Fail!";
            }
        }
        return "Initialize Game Complete!";
    }

    @GetMapping("/play")
    public String play() {
        if (initialize) {
            if (!playing) {
                if (words.size() > playerCount) {
                    playing = true;
                    round++;
                    int min = 0;
                    int max = words.size()-1;
                    for (Player player : playerList) {
                        int index = (int)(Math.random()*(max-min+1)+min);
                        player.setWord(words.remove(index));
                        max--;
                    }
                    return "Now Playing!, Round "+ round +" Start!";
                }
                return "Not enough words for players!";
            }
            return "Now Playing!, Round "+ round +", Please stop before play again.";
            }

        else {
            return "Please Initialize Game!";
        }
    }

    @PostMapping("/update-score")
    public String updateScore(@RequestBody UpdateScore updateScore) {
        for (String killDetail : updateScore.getKillList()) {
            String[] killDetailSplit = killDetail.split("-");
            int killId = Integer.parseInt(killDetailSplit[0]);
            int deadId = Integer.parseInt(killDetailSplit[1]);
            if (0 < killId && killId <= playerCount && 0 < deadId && deadId <= playerCount) {
                Player kill = playerList.get(killId-1);
                Player dead = playerList.get(deadId-1);
                kill.setKillScore(kill.getKillScore()+1);
                dead.setDeadCount(dead.getDeadCount()+1);
            }
        }

        for (Integer id: updateScore.getGuessList()) {
            if (0 < id && id <= playerCount) {
                Player player = playerList.get(id-1);
                player.setGuessScore(player.getGuessScore()+1);
            }
        }

        for (Integer id: updateScore.getDeadList()) {
            if (0 < id && id <= playerCount) {
                Player player = playerList.get(id-1);
                player.setDeadCount(player.getDeadCount()+1);
            }
        }

        return "Update Score Complete!";
    }

    @GetMapping("/stop")
    public String stop() {
        playing = false;
        return "Stop Playing!";
    }

    @GetMapping("/score")
    public String score() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<pre>\n");
        stringBuilder.append("***** Score Board *****\n");
        stringBuilder.append("====================\n");
        for (Player player : playerList) {
            stringBuilder.append(player.getScoreDetail()).append("\n");
        }
        stringBuilder.append("====================\n");
        stringBuilder.append("</pre>");
        return stringBuilder.toString();
    }

    @GetMapping("/clear")
    public String clear() {
        playing = false;
        playerCount = 0;
        round = 0;
        playerMap = new HashMap<>();
        playerList = new ArrayList<>();
        return "Clear Game Complete!";
    }

    @GetMapping("/player/{uuid}")
    public String player(@PathVariable String uuid) {
        StringBuilder stringBuilder = new StringBuilder();
        if (playerMap.containsKey(uuid)) {
            if (playing) {
                stringBuilder.append("<pre>\n");
                stringBuilder.append("***** Round ").append(round).append(" *****\n");
                stringBuilder.append("====================\n");
                for (Player player : playerList) {
                    if (!player.getUuid().equals(uuid)) {
                        stringBuilder.append(player.getId()).append("-").append(player.getName()).append(": [").append(player.getWord()).append("]\n");
                    }
                    else {
                        stringBuilder.append(player.getId()).append("-").append(player.getName()).append(": [?]\n");
                    }
                }
                stringBuilder.append("====================\n");
                stringBuilder.append("</pre>");
                stringBuilder.append(score());
            }
            else {
                return "<pre>Please Waiting Next Round.\n</pre>"+score();
            }
        }
        else {
            stringBuilder.append("Invalid UUID.");
        }
        return stringBuilder.toString();
    }

}
