package com.yjkmust.arcgisdemo.Option;

import com.esri.core.geometry.Point;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class Transform {
    public static BursaSevenParams params = new BursaSevenParams();
    // public static Point pCp = new Point();
    // public static BursaSevenParams params = new
    // BursaSevenParams(0.000275698610,0.0003800999,-0.0016414186,-0.0007927424,359.015741,-1453.243413,-691.773742);
//	public static BursaSevenParams params = new BursaSevenParams(
//			0.00028138635200, Transform.degree2rad(new BigDecimal(76.88182379 / 3600.0))
//					.doubleValue(), Transform.degree2rad(
//					new BigDecimal(-336.71947876 / 3600.0)).doubleValue(),
//					Transform.degree2rad(new BigDecimal(-166.35757847 / 3600.0)).doubleValue(),
//			461.673674, -1420.031941, -723.766552);
    public static BigDecimal PI = new BigDecimal(3.14159265358979323846);

    /*
     * 经纬度转换为地心坐标（空间直角坐标） point 变换的坐标点 semiMajorAxis 椭球体长轴 semiMinorAxis 椭球体短轴
     */
    public static Point Ellipsoid2Geocentric(Point pCp, double semiMajorAxis,
                                             double semiMinorAxis) {
        double r_maj = semiMajorAxis;// 主半轴
        double r_min = semiMinorAxis;// 次半轴
        double es = 1.0 - pow(r_min / r_maj, 2);// 第一偏心率的平方
        double con = 1.0 - es * pow(sin(pCp.getY()), 2);// 用于计算N值的分母
        double n = r_maj / sqrt(con);// N值(卯酉圈曲率半径)
        Point newCp = new Point();
        newCp.setX((n + pCp.getZ()) * cos(pCp.getY()) * cos(pCp.getX()));
        newCp.setY((n + pCp.getZ()) * cos(pCp.getY()) * sin(pCp.getX()));
        newCp.setZ(((1 - es) * n + pCp.getZ()) * sin(pCp.getY()));
        // double a1=newCp->m_pOrd->at(0);
        // double a2=newCp->m_pOrd->at(1);
        // double a3=newCp->m_pOrd->at(2);
        return newCp;
    }

    public static BursaSevenParams ComputeBursaSevenParam(
            List<Point> srcPoints, List<Point> destPoints) throws Exception {
        BursaSevenParams param = new BursaSevenParams();
        if (srcPoints.size() != destPoints.size()) {
            throw new Exception("坐标数不匹配");
        }
        int size = srcPoints.size();
        Matrix matrix = new Matrix(size, 6);
        Matrix matrixA = new Matrix(size * 3, 7);
        Matrix matrixL = new Matrix(size * 3, 1);
        for (int i = 0; i < size; i++) {
            matrix.setValues(i, 0, srcPoints.get(i).getX());
            matrix.setValues(i, 1, srcPoints.get(i).getY());
            matrix.setValues(i, 2, srcPoints.get(i).getZ());
            matrix.setValues(i, 3, destPoints.get(i).getX());
            matrix.setValues(i, 4, destPoints.get(i).getY());
            matrix.setValues(i, 5, destPoints.get(i).getZ());

        }
        int index = 0;
        for (int i = 0; i < size; i++) {
            double x1 = matrix.getValue(i, 0);
            double y1 = matrix.getValue(i, 1);
            double z1 = matrix.getValue(i, 2);

            double x2 = matrix.getValue(i, 3);
            double y2 = matrix.getValue(i, 4);
            double z2 = matrix.getValue(i, 5);

            matrixA.setValues(3 * i, 0, 1);
            matrixA.setValues(3 * i, 1, 0);
            matrixA.setValues(3 * i, 2, 0);
            matrixA.setValues(3 * i, 3, x1);
            matrixA.setValues(3 * i, 4, 0);
            matrixA.setValues(3 * i, 5, -z1);
            matrixA.setValues(3 * i, 6, y1);

            matrixA.setValues(3 * i + 1, 0, 0);
            matrixA.setValues(3 * i + 1, 1, 1);
            matrixA.setValues(3 * i + 1, 2, 0);
            matrixA.setValues(3 * i + 1, 3, y1);
            matrixA.setValues(3 * i + 1, 4, z1);
            matrixA.setValues(3 * i + 1, 5, 0);
            matrixA.setValues(3 * i + 1, 6, -x1);

            matrixA.setValues(3 * i + 2, 0, 0);
            matrixA.setValues(3 * i + 2, 1, 0);
            matrixA.setValues(3 * i + 2, 2, 1);
            matrixA.setValues(3 * i + 2, 3, z1);
            matrixA.setValues(3 * i + 2, 4, -y1);
            matrixA.setValues(3 * i + 2, 5, x1);
            matrixA.setValues(3 * i + 2, 6, 0);

            matrixL.setValues(index++, 0, x2);
            matrixL.setValues(index++, 0, y2);
            matrixL.setValues(index++, 0, z2);
        }
        Matrix at = Matrix.bitwise_not(matrixA);
        Matrix ata = Matrix.mul(at, matrixA);
        Matrix atl = Matrix.mul(at, matrixL);
        Matrix invata = Matrix.excalmatory_mark(ata);
        Matrix matrixX = Matrix.mul(invata, atl);
        param.Dx = matrixX.getValue(0, 0);
        param.Dy = matrixX.getValue(1, 0);
        param.Dz = matrixX.getValue(2, 0);
        param.Ppm = matrixX.getValue(3, 0);
        param.Ex = matrixX.getValue(4, 0) / param.Ppm;
        param.Ey = matrixX.getValue(5, 0) / param.Ppm;
        param.Ez = matrixX.getValue(6, 0) / param.Ppm;
        return param;
    }

    // /执行7参数变换
    // /pCp 待变换的点
    // /params 变换7参数
    public static Point BursaTransform(Point pCp) throws Exception {
        Point newCp = new Point();

        // double M = 1 + params.Ppm * 0.000001;
        // newCp.setX(params.Dx
        // + (M * (1 * pCp.getX() - params.Ez * pCp.getY() + params.Ey
        // * pCp.getZ())));
        // newCp.setY(params.Dy
        // + (M * (params.Ez * pCp.getX() + 1 * pCp.getY() - params.Ex
        // * pCp.getZ())));
        // newCp.setZ(params.Dz
        // + (M * (-params.Ey * pCp.getX() + params.Ex * pCp.getY() + 1 * pCp
        // .getZ())));
        Matrix param = new Matrix(3, 7);
        param.setValues(0, 0, 1.0);
        param.setValues(0, 1, 0.0);
        param.setValues(0, 2, 0.0);
        param.setValues(0, 3, pCp.getX());
        param.setValues(0, 4, 0.0);
        param.setValues(0, 5, -pCp.getZ());
        param.setValues(0, 6, pCp.getY());

        param.setValues(1, 0, 0.0);
        param.setValues(1, 1, 1.0);
        param.setValues(1, 2, 0.0);
        param.setValues(1, 3, pCp.getY());
        param.setValues(1, 4, pCp.getZ());
        param.setValues(1, 5, 0);
        param.setValues(1, 6, -pCp.getX());

        param.setValues(2, 0, 0.0);
        param.setValues(2, 1, 0.0);
        param.setValues(2, 2, 1.0);
        param.setValues(2, 3, pCp.getZ());
        param.setValues(2, 4, -pCp.getY());
        param.setValues(2, 5, pCp.getX());
        param.setValues(2, 6, 0.0);

        Matrix sevenMatrix = new Matrix(7, 1);
        sevenMatrix.setValues(0, 0, params.Dx);
        sevenMatrix.setValues(1, 0, params.Dy);
        sevenMatrix.setValues(2, 0, params.Dz);
        sevenMatrix.setValues(3, 0, params.Ppm);
        sevenMatrix.setValues(4, 0, params.Ex * params.Ppm);
        sevenMatrix.setValues(5, 0, params.Ey * params.Ppm);
        sevenMatrix.setValues(6, 0, params.Ez * params.Ppm);

        Matrix result = Matrix.mul(param, sevenMatrix);
        newCp.setX(result.getValue(0, 0));
        newCp.setY(result.getValue(1, 0));
        newCp.setZ(result.getValue(2, 0));
        return newCp;
    }

    // /地心坐标（空间直角坐标）变换为经纬度坐标
    // /pCp 待变换的点
    // /semiMajorAxis 椭球体长轴
    // /inverseFlattening 椭球体扁率
    public static Point Geocentric2Ellipsoid(Point pCp, double semiMajorAxis,
                                             double inverseFlattening) {
        Point newCp = new Point();
        double x = pCp.getX();
        double y = pCp.getY();
        double z = pCp.getZ();
        double lon, lat, h;
        double _a = semiMajorAxis;
        double _maxrad = 2 * _a / Double.MIN_VALUE;
        double f = inverseFlattening;
        double _f = f <= 1 ? f : 1 / f;
        double _e2 = _f * (2 - _f);
        double _e4a = pow((double) _e2, (double) 2.0f);
        double _e2m = pow((double) 1 - _f, (double) 2.0f);
        double _e2a = abs(_e2);
        double R = hypot(x, y), slam = R != 0 ? y / R : 0, clam = R != 0 ? x
                / R : 1;
        h = hypot(R, z); // Distance to center of earth
        double sphi, cphi;
        if (h > _maxrad) {
            // We really far away (> 12 million light years); treat the earth as
            // a
            // point and h, above, is an acceptable approximation to the height.
            // This avoids overflow, e.g., in the computation of disc below.
            // It's
            // possible that h has overflowed to inf; but that's OK.
            //
            // Treat the case x, y finite, but R overflows to +inf by scaling by
            // 2.
            R = hypot(x / 2, y / 2);
            slam = R != 0 ? (y / 2) / R : 0;
            clam = R != 0 ? (x / 2) / R : 1;
            double H = hypot(z / 2, R);
            sphi = (z / 2) / H;
            cphi = R / H;
        } else if (_e4a == 0) {
            // Treat the spherical case. Dealing with underflow in the general
            // case
            // with _e2 = 0 is difficult. Origin maps to N pole same as with
            // ellipsoid.
            double H = hypot(h == 0 ? 1 : z, R);
            sphi = (h == 0 ? 1 : z) / H;
            cphi = R / H;
            h -= _a;
        } else {
            // Treat prolate spheroids by swapping R and z here and by switching
            // the arguments to phi = atan2(...) at the end.
            double p = pow(R / _a, 2), q = _e2m * pow(z / _a, 2), r = (p + q - _e4a) / 6;
            if (_f < 0) {
                double temp;
                temp = p;
                p = q;
                q = temp;
                // swap(p, q);
            }
            if (!(_e4a * q == 0 && r <= 0)) {
                double
                        // Avoid possible division by zero when r = 0 by multiplying
                        // equations for s and t by r^3 and r, resp.
                        S = _e4a * p * q / 4, // S = r^3 * s
                        r2 = pow(r, 2), r3 = r * r2, disc = S * (2 * r3 + S);
                double u = r;
                if (disc >= 0) {
                    double T3 = r3 + S;
                    // Pick the sign on the sqrt to maximize abs(T3). This
                    // minimizes
                    // loss of precision due to cancellation. The result is
                    // unchanged
                    // because of the way the T is used in definition of u.
                    T3 += T3 < 0 ? -sqrt(disc) : sqrt(disc); // T3 = (r * t)^3
                    // N.B. cbrt always returns the real root. cbrt(-8) = -2.
                    double T = pow(T3, 1.0 / 3.0); // T = r * t
                    // T can be zero; but then r2 / T -> 0.
                    u += T + (T != 0 ? r2 / T : 0);
                } else {
                    // T is complex, but the way u is defined the result is
                    // real.
                    double ang = atan2(sqrt(-disc), -(S + r3));
                    // There are three possible cube roots. We choose the root
                    // which
                    // avoids cancellation. Note that disc < 0 implies that r <
                    // 0.
                    u += 2 * r * cos(ang / 3);
                }
                double v = sqrt(u * u + _e4a * q), // guaranteed positive
                        // Avoid loss of accuracy when u < 0. Underflow doesn't occur in
                        // e4 * q / (v - u) because u ~ e^4 when q is small and u < 0.
                        uv = u < 0 ? _e4a * q / (v - u) : u + v, // u+v, guaranteed
                        // positive
                        // Need to guard against w going negative due to roundoff in uv
                        // - q.
                        w = (_e2a * (uv - q) / (2 * v) < 0.0f ? 0.0f : _e2a * (uv - q)
                                / (2 * v)),
                        // Rearrange expression for k to avoid loss of accuracy due to
                        // subtraction. Division by 0 not possible because uv > 0, w >=
                        // 0.
                        k = uv / (sqrt(uv + w * w) + w), k1 = _f >= 0 ? k : k - _e2, k2 = _f >= 0 ? k
                        + _e2
                        : k, d = k1 * R / k2, H = hypot(z / k1, R / k2);
                sphi = (z / k1) / H;
                cphi = (R / k2) / H;
                h = (1 - _e2m / k1) * hypot(d, z);
            } else { // e4 * q == 0 && r <= 0
                // This leads to k = 0 (oblate, equatorial plane) and k + e^2 =
                // 0
                // (prolate, rotation axis) and the generation of 0/0 in the
                // general
                // formulas for phi and h. using the general formula and
                // division by 0
                // in formula for h. So handle this case by taking the limits:
                // f > 0: z -> 0, k -> e2 * sqrt(q)/sqrt(e4 - p)
                // f < 0: R -> 0, k + e2 -> - e2 * sqrt(q)/sqrt(e4 - p)
                double zz = sqrt((_f >= 0 ? _e4a - p : p) / _e2m), xx = sqrt(_f < 0 ? _e4a
                        - p
                        : p), H = hypot(zz, xx);
                sphi = zz / H;
                cphi = xx / H;
                if (z < 0)
                    sphi = -sphi; // for tiny negative z (not for prolate)
                h = -_a * (_f >= 0 ? _e2m : 1) * H / _e2a;
            }
        }
        lat = atan2(sphi, cphi);
        lon = -atan2(-slam, clam);

        newCp.setX(lon);
        newCp.setY(lat);
        newCp.setZ(h);
        return newCp;
    }

    public static BigDecimal degree2rad(BigDecimal degree) {
        return degree.divide(new BigDecimal(180), 16, RoundingMode.HALF_UP)
                .multiply(PI);
    }

    public static double rad2degree(double rad) {
        return rad / PI.doubleValue() * 180;
    }
}