package com.fhf.spring.IOC;

import lombok.Data;

import java.util.List;

@Data
public class Bicycle {

    private Wheel[] wheels;

    private String brand;

    private List<Mounting> mountings;

}
