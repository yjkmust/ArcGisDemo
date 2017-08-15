package com.yjkmust.arcgisdemo.Option;

import java.io.Serializable;

/**
 * Created by Shyam on 2016/9/2.
 */
public class BursaSevenParams implements Serializable {
    public static final long serialVersionUID = -7060210544600464481L;
    public double Ppm, Ex, Ey, Ez, Dx, Dy, Dz;
    public BursaSevenParams() {
    }
    public BursaSevenParams(double Ppm, double Ex, double Ey, double Ez,
                            double Dx, double Dy, double Dz) {
        this.Ppm = Ppm;
        this.Ex = Ex;
        this.Ey = Ey;
        this.Ez = Ez;
        this.Dx = Dx;
        this.Dy = Dy;
        this.Dz = Dz;
    }
}