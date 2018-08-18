package com.iddqdeika.wagoncounter.logicHandlers;

import com.iddqdeika.wagoncounter.DAObjects.TrainRepo;
import com.iddqdeika.wagoncounter.entities.Train;
import com.iddqdeika.wagoncounter.entities.Wagon;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.html.HTMLDocument;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class TrainWagonsCountHandler extends Thread {

    TrainRepo trainRepo;
    private Train train;
    Wagon[] wagons;
    boolean startingLight;
    Wagon current;
    int pos;


    public TrainWagonsCountHandler(Train train, TrainRepo trainRepo){
        this.trainRepo = trainRepo;
        this.train = train;
        this.wagons = train.getWagons().toArray( new Wagon[train.getWagons().size()]);
        pos = train.getStartingPos();
        current = wagons[pos];
        startingLight = current.getLigntOn();
    }

    @Override
    public void run() {
        train.setWagonsCount(getTrainLength());
        trainRepo.save(train);
    }

    public int getTrainLength(){
        int len = 0;
        int lastlen = 0;
        current.switchLight();
        while(current.getLigntOn()!=startingLight){
            do{
                next();
                len++;
            }while (current.getLigntOn()==startingLight);
            current.switchLight();
            lastlen = len;
            while (len>0){
                prev();
                len--;
            }
        }
        return lastlen;
    }

    private void next(){
        if (pos<wagons.length-1){
            pos++;
        }else{
            pos = 0;
        }
        current = wagons[pos];
    }

    private void prev(){
        if (pos>0){
            pos--;
        }else{
            pos = wagons.length-1;
        }
        current = wagons[pos];
    }




}
