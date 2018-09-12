package com.kloudtek.anypoint.cloudhub;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class CHWorkerType {
    private String name;
    private BigDecimal workerVal;
    private BigDecimal weight;
    private String cpu;
    private String memory;

    @JsonProperty
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public BigDecimal getWorkerVal() {
        return workerVal;
    }

    public void setWorkerVal(BigDecimal workerVal) {
        this.workerVal = workerVal;
    }

    @JsonProperty
    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    @JsonProperty
    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    @JsonProperty
    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }
}
