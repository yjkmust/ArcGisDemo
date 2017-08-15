package com.yjkmust.arcgisdemo.Option;

import android.location.Location;

import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

import java.math.BigDecimal;



/**
 * Created by GEOFLY on 2017/8/10.
 */

public class ProjectionClass {
    // 北京54长短轴
    public static final double LongAxis_Beijing54 = 6378245.0;
    public static final double ShortAxis_Beijing54 = 6356863.0187730473;
    public static final double Malalignment_Beijing54 = 298.3;// 扁率

    // 西安80长短轴
    public static final double LongAxis_Xian80 = 6378140.0;
    public static final double ShortAxis_Xian80 = 6356755.2881575283;
    public static final double Malalignment_Xian80 = 298.257;// 偏心率

    // WGS84长短轴
    public static final double LongAxis_WGS84 = 6378137.0;
    public static final double ShortAxis_WGS84 = 6356752.3142;
    public static final double Malalignment_WGS84 = 298.257223563;// 扁率

    /**
     * 投影到WGS84
     * @param location
     * @return
     */
    public static Point projectionGPS(final Location location, final SpatialReference sr){
        Point ptBL = new Point(location.getLongitude(), location.getLatitude(),
                location.getAltitude());
        SpatialReference sr4326 = SpatialReference.create(2410);
        Point ptProject = (Point) GeometryEngine.project(ptBL,
                sr, sr4326);
        ptProject.setX(ptProject.getX());
        ptProject.setY(ptProject.getY());
        ptProject.setZ(ptProject.getZ());
        return ptProject;
    }

    /**
     * 转换投影到KM87
     * @param location
     * @return
     */
    public static Point projectionLocationForKM87(final Location location) {
        Point p = new Point(location.getLongitude(), location.getLatitude(),
                location.getAltitude());
        Point projectPoint;
        Point result = null;
        Transform.params.Ppm = 1.00028138635200;
        Transform.params.Ex = Transform.degree2rad(
                new BigDecimal(76.88182379 / 3600.0)).doubleValue();
        Transform.params.Ey = Transform.degree2rad(
                new BigDecimal(-336.71947876 / 3600.0)).doubleValue();
        Transform.params.Ez = Transform.degree2rad(
                new BigDecimal(-166.35757847 / 3600.0)).doubleValue();
        Transform.params.Dx = 461.673674;
        Transform.params.Dy = -1420.031941;
        Transform.params.Dz = -723.766552;
        Point point = new Point(Transform.degree2rad(
                new BigDecimal(location.getLongitude())).doubleValue(),
                Transform
                        .degree2rad(new BigDecimal(location.getLatitude()))
                        .doubleValue(), location.getAltitude());
        Point point0 = Transform.Ellipsoid2Geocentric(point,
                LongAxis_WGS84,
                ShortAxis_WGS84);
        // Point point0 =new Point(6379976.997542, 84.380328, 155.170210);
        Point point1 = null;
        try {
            point1 = Transform.BursaTransform(point0);
        } catch (Exception e) {
            return null;
        }
        Point point2 = null;
        point2 = Transform.Geocentric2Ellipsoid(point1,
                LongAxis_Beijing54,
                Malalignment_Beijing54);

        projectPoint = new Point(Transform.rad2degree(point2.getX()),
                Transform.rad2degree(point2.getY()), point2.getZ());

        String wkText = "PROJCS[\"WGS84_3_Degree_GK_CM_102E\",GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\",6378137.0,298.257223563]],PRIMEM[\"Greenwich\",0.0],UNIT[\"Degree\",0.0174532925199433]],PROJECTION[\"Gauss_Kruger\"],PARAMETER[\"False_Easting\",865601.9],PARAMETER[\"False_Northing\",-198704.1],PARAMETER[\"Central_Meridian\",102.5],PARAMETER[\"Scale_Factor\",1.0],PARAMETER[\"Latitude_Of_Origin\",0.0],UNIT[\"Meter\",1.0]]";
        SpatialReference wgs84_out = SpatialReference.create(wkText);
        SpatialReference wgs84_in = SpatialReference.create(4326);
        result = (Point) GeometryEngine.project(projectPoint, wgs84_in,
                wgs84_out);
        return result;
    }
}
