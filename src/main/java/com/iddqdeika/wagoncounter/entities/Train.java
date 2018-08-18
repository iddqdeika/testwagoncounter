package com.iddqdeika.wagoncounter.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Train")
public class Train {

    public Train(JsonObject body){
        startingPos = body.get("startingPosition").getAsInt();
        JsonArray wagonsArray = body.get("wagons").getAsJsonArray();
        Set<Wagon> wagons = new HashSet<>();
        for (int i = 0; i<wagonsArray.size(); i++){
            JsonObject wagonObj = wagonsArray.get(i).getAsJsonObject();
            wagons.add(new Wagon(i,wagonObj.get("lightON").getAsBoolean(),this));
        }
        this.setWagons(wagons);
    }

    public Train(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "WagonsCount")
    private Integer wagonsCount = null;

    @Column(name = "StartingPosition")
    private Integer startingPos;

    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL)
    private Set<Wagon> wagons;

    public Integer getStartingPos() {
        return startingPos;
    }

    public void setWagonsCount(Integer wagonsCount) {
        this.wagonsCount = wagonsCount;
    }

    public Integer getWagonsCount() {
        return wagonsCount;
    }

    public Integer getId() {
        return id;
    }

    public Set<Wagon> getWagons(){
        return wagons;
    }

    public void setWagons(Set<Wagon> wagons) {
        this.wagons = wagons;
    }
}
