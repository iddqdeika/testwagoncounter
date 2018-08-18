package com.iddqdeika.wagoncounter.mappings;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iddqdeika.wagoncounter.DAObjects.TrainRepo;
import com.iddqdeika.wagoncounter.entities.Train;
import com.iddqdeika.wagoncounter.entities.Wagon;
import com.iddqdeika.wagoncounter.logicHandlers.TrainWagonsCountHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Controller
public class WagonCounter {

    @Autowired
    private TrainRepo trainRepo;

    /**
    @RequestMapping(name = "/")
    public ResponseEntity<String> getWagons(){

        Train train = new Train();
        Set wagons = new HashSet<Wagon>();
        for (int i = 0; i<5; i++){
            wagons.add(new Wagon(i,true,train));
        }
        train.setWagons(wagons);
        trainRepo.save(train);

        ArrayList<Train> trains = new ArrayList(trainRepo.findAll());
        JsonObject body = new JsonObject();
        JsonArray trainsArray = new JsonArray();
        for (int i = 0; i<trains.size(); i++){
            JsonObject trainObj = new JsonObject();
            Train tempTrain = trains.get(i);
            ArrayList<Wagon> tempWagons = new ArrayList<>();
            Set wagonset = tempTrain.getWagons();
            for (Object twagon : wagonset.toArray()){
                tempWagons.add((Wagon) twagon);
            }
            trainObj.addProperty("trainID",tempTrain.getId());
            trainObj.add("wagons",getWagonList(tempWagons));
            trainsArray.add(trainObj);
        }
        body.add("trains",trainsArray);
        String response = body.toString();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    */
    @RequestMapping(value = "/")
    public ResponseEntity<String> checkthis(){
        try {
            String str = "{startingPosition:0,\n" +
                    "wagons:[\n{lightON:true}";
            Random random = new Random();
            int size = random.nextInt(100);
            for (int i = 0; i < size; i++) {
                str += ", \r\n{lightON:" + random.nextBoolean() + "}";
            }
            str += "]}";
            size++;
            String server = System.getenv("server");
            String response = sendPost(server + "/train", str);
            JsonParser parser = new JsonParser();
            JsonObject body = parser.parse(response).getAsJsonObject();
            int id = body.get("id").getAsInt();
            String url = server + "/train?id="+id;
            long now = System.currentTimeMillis();
            while ((System.currentTimeMillis()-now)<2000){

            }
            response = sendGet(url);
            int res = parser.parse(response).getAsJsonObject().get("count").getAsInt();
            if (res==size){
                return new ResponseEntity("for test string:\r\n" + str + "\r\n with size = " + size + " test completed", HttpStatus.OK);
            }else{
                return new ResponseEntity("for test string:\r\n" + str + "\r\n with size = " + size + " test crushed", HttpStatus.OK);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return new ResponseEntity("ERROR: " + ex.getMessage(), HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/train", method = RequestMethod.POST)
    public ResponseEntity<String> createTrain(@RequestBody String body){
        JsonParser parser = new JsonParser();
        ResponseEntity response;
        try{
            JsonObject bodyObj = parser.parse(body).getAsJsonObject();
            Train train = new Train(bodyObj);
            trainRepo.save(train);
            response = new ResponseEntity<>("{\"id\":" + train.getId().toString() + "}",HttpStatus.CREATED);
            TrainWagonsCountHandler handler = new TrainWagonsCountHandler(train, trainRepo);
            handler.start();
        }catch (Exception ex){
            ex.printStackTrace();
            response = new ResponseEntity<>(new String("Incorrect Json: \"" + body + "\""),HttpStatus.CONFLICT);
        }

        return response;
    }

    @RequestMapping(value = "/train", method = RequestMethod.GET)
    public ResponseEntity<String> getTrainLength(@RequestParam(name = "id") int id){
        Train train = trainRepo.getOne(id);
        ResponseEntity response;
        if (train!=null){
            if (train.getWagonsCount()!=null) {
                return new ResponseEntity("{\"id\":" + id + ",count:" + train.getWagonsCount() + "}", HttpStatus.OK);
            }{
                return new ResponseEntity("{message:\"wagon counting incomplete\"}",HttpStatus.OK);
            }
        }else{
            return new ResponseEntity("{message:\"train does not exist\"}",HttpStatus.OK);
        }

    }

    public String sendPost(String urlString, String body){
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length",  String.valueOf(body.length()));

            OutputStream os = connection.getOutputStream();
            os.write(body.getBytes());
            StringBuilder responseSB = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ( (line = br.readLine()) != null)
                responseSB.append(line);

            br.close();
            os.close();

            return responseSB.toString();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public String sendGet(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");

            connection.connect();

            StringBuilder responseSB = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ( (line = br.readLine()) != null)
                responseSB.append(line);

            br.close();
            return responseSB.toString();
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    private JsonArray getWagonList(ArrayList<Wagon> wagons){
        JsonArray wagonArray = new JsonArray();
        int size = wagons.size();
        for (int i = 0; i<size; i++){
            wagonArray.add(wagons.get(i).getJsonObject());
        }
        return wagonArray;
    }


}
