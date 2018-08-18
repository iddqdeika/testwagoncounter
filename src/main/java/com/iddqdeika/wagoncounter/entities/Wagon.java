package com.iddqdeika.wagoncounter.entities;


import com.google.gson.JsonObject;

import javax.persistence.*;

@Entity
@Table(name = "Wagon")
public class Wagon {

    protected Wagon(){

    }

    public Wagon(int sequence, boolean ligntOn, Train train){
        super();
        this.sequence = sequence;
        this.ligntOn = ligntOn;
        this.train = train;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Column(name = "Sequence",nullable = false)
    private Integer sequence;

    @Column(name = "LightON", nullable = false)
    private Boolean ligntOn;

    @ManyToOne
    @JoinColumn(name = "TrainID")
    private Train train;

    public void setTrain(Train train) {
        this.train = train;
    }

    public Train getTrain() {
        return train;
    }

    public Boolean getLigntOn() {
        return ligntOn;
    }

    public void switchLight(){
        ligntOn = !ligntOn;
    }

    @Override
    public String toString() {
        return "wagon " + this.id;
    }

    public JsonObject getJsonObject(){
        JsonObject body = new JsonObject();
        body.addProperty("ID",this.id);
        body.addProperty("Sequence", this.sequence);
        body.addProperty("LightON", this.ligntOn);
        return body;
    }

}
