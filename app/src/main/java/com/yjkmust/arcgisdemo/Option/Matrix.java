package com.yjkmust.arcgisdemo.Option;

import java.util.Vector;

/**
 * Created by Shyam on 2016/9/2.
 */
public class Matrix {
    public Vector<Double> Elt = new Vector<Double>();
    public int m_nRowNo;
    public int m_nColumnNo;

    // private static Vector<Double> values;

    public Matrix(Vector<Double> values, int nRowNo, int nColumnNo)
            throws Exception {
        // this.values = values;
        if (nRowNo < 1 || nColumnNo < 1) {
            throw new Exception("values错误的矩阵行或列");
        }
        if (values.size() < nRowNo * nColumnNo) {
            throw new Exception("values数量不够");
        }

        m_nRowNo = nRowNo;
        m_nColumnNo = nColumnNo;
        Elt.clear();
        Elt.addAll(values);
    }

    public Matrix(int nRowNo, int nColumnNo) throws Exception {
        if (nRowNo < 1 || nColumnNo < 1) {
            throw new Exception("values错误的矩阵行或列");
        }

        m_nRowNo = nRowNo;
        m_nColumnNo = nColumnNo;
        Elt.clear();
        Elt.setSize(nRowNo * nColumnNo);
    }

    public Matrix(Matrix matrix) throws Exception {
        m_nRowNo = matrix.m_nRowNo;
        m_nColumnNo = matrix.m_nColumnNo;
        Elt.clear();
        Elt.addAll(matrix.Elt);
    }

    public int getRowNo() {
        return m_nRowNo;
    }

    public int getColumnNo() {
        return m_nColumnNo;
    }

    public void setSize(int nRowNo, int nColumnNo) {
        m_nRowNo = nRowNo;
        m_nColumnNo = nColumnNo;
        Elt.clear();
        Elt.setSize(nRowNo * nColumnNo);
    }

    public void setValues(Vector<Double> values) throws Exception {
        // this.values = values;
        int count = m_nColumnNo * m_nRowNo;
        if (values.size() < count) {
            throw new Exception("values值少于当前矩阵数量");
        }
        Elt.clear();
        Elt.addAll(values);
    }

    public void setValues(int row, int col, double value) throws Exception {
        if (row >= m_nRowNo || col >= m_nColumnNo) {
            throw new Exception("row or col超出范围");
        }
        Elt.set(row * m_nColumnNo + col, value);
    }

    public double getValue(int nRowNo, int nColumnNo) throws Exception {
        if (nRowNo >= m_nRowNo) {
            throw new Exception("nRowNo超出范围");
        }

        if (nColumnNo >= m_nColumnNo) {
            throw new Exception("nColumnNo超出范围");
        }
        return Elt.get(nRowNo * m_nColumnNo + nColumnNo);
    }

    public Matrix inversion() throws Exception {
        int i = 0, j = 0, k = 0;
        if (m_nColumnNo != m_nRowNo) {
            throw new Exception("该矩阵不能进行逆矩阵运算");
        }
        Matrix newMatrix = new Matrix(m_nRowNo, m_nColumnNo);
        newMatrix.unit();

        for (k = 0; k < m_nRowNo; k++) {
            int index = pivot(k);
            if (index == -1) {
                throw new Exception("奇异矩阵");
            }
            if (index != 0) {
                newMatrix.exchangeRow(k, index);
            }
            double a1 = getValue(k, k);
            for (j = 0; j < m_nRowNo; j++) {
                setValues(k, j, getValue(k, j) / a1);
                newMatrix.setValues(k, j, newMatrix.getValue(k, j) / a1);
            }
            for (i = 0; i < m_nRowNo; i++) {
                if (i != k) {
                    double a2 = getValue(i, k);
                    for (j = 0; j < m_nRowNo; j++) {
                        setValues(i, j, getValue(i, j) - a2 * getValue(k, j));
                        newMatrix.setValues(i, j, newMatrix.getValue(i, j) - a2
                                * newMatrix.getValue(k, j));
                    }
                }
            }
        }
        return newMatrix;
    }

    public void exchangeRow(int row1, int row2) throws Exception {
        if (row1 >= m_nRowNo || row2 >= m_nRowNo) {
            throw new Exception("指定的行超出范围");
        }
        // int begin1 = row1 * m_nColumnNo, begin2 = row2 * m_nColumnNo;
        // Vector<Double> temp = new Vector<Double>(0 + begin1, 0 + begin1
        // + m_nColumnNo);
        // for (int i = begin1, k = begin2, j = 0; i < begin1 + m_nColumnNo;
        // i++, k++, j++) {
        // Elt.set(i, Elt.get(k));
        // Elt.set(k, temp.get(j));
        // }
        int begin1 = row1 * m_nColumnNo, begin2 = row2 * m_nColumnNo;
        Double tmp;
        for (int i = begin1, k = begin2, j = 0; i < begin1 + m_nColumnNo; i++, k++, j++) {
            tmp = Elt.get(i);
            Elt.set(i, Elt.get(k));
            Elt.set(k, tmp);
        }
    }

    public void unit() {
        // int row = Math.min(m_nColumnNo, m_nRowNo);
        // m_nColumnNo = m_nRowNo = row;
        // for (int i = 0; i < m_nRowNo; i++) {
        // for (int j = 0; j < m_nColumnNo; j++) {
        // Elt.set(i * row + j, i == j ? 1.0 : 0.0);
        // // Elt[i * row + j] = i == j ? 1.0 : 0.0;
        // }
        // }
        int row = Math.min(m_nColumnNo, m_nRowNo);
        m_nColumnNo = m_nRowNo = row;
        for (int i = 0; i < m_nRowNo; i++) {
            for (int j = 0; j < m_nColumnNo; j++) {
                Double val = i == j ? 1.0 : 0.0;
                Elt.setElementAt(val, i * row + j);
            }
        }
    }

    public void unit(int row) {
        if (row != m_nColumnNo || row != m_nRowNo) {
            setSize(row, row);
        }
        for (int i = 0; i < m_nRowNo; i++) {
            for (int j = 0; j < m_nColumnNo; j++) {
                Elt.set(i * row + j, i == j ? 1.0 : 0.0);
                // Elt[i * row + j] = i == j ? 1.0 : 0.0;
            }
        }
    }

    public int pivot(int row) throws Exception {
        int k = row;
        double amax = -1, temp;
        int i = 0;
        for (i = row; i < m_nRowNo; i++) {
            if ((temp = Math.abs(getValue(i, row))) > amax && temp != 0.0) {
                amax = temp;
                k = i;
            }
        }

        if (getValue(k, row) == 0.0)
            return -1;
        if (k != row) {
            exchangeRow(k, row);
            return k;
        }
        return 0;
    }

    /**
     * -()运算
     * */
    public Matrix sub_bracket() throws Exception {
        Matrix matrix = new Matrix(this);
        for (int i = 0; i < m_nRowNo; i++) {
            for (int j = 0; j < m_nColumnNo; j++) {
                matrix.setValues(i, j, -getValue(i, j));
            }
        }
        return matrix;
    }

    /**
     * +=运算
     * */
    public Matrix add_equal(Matrix matrix) throws Exception {
        if (m_nColumnNo != matrix.m_nColumnNo || m_nRowNo != matrix.m_nRowNo) {
            throw new Exception("matrix相加两矩阵行列不匹配");
        }
        for (int i = 0; i < Elt.size(); i++) {
            Elt.set(i, Elt.get(i) + matrix.Elt.get(i));
        }
        return this;
    }

    /**
     * ~(按位非)运算
     * */
    public static Matrix bitwise_not(Matrix matrix) throws Exception {
        Matrix m = matrix;
        int row = m.getRowNo(), col = m.getColumnNo();
        Matrix newMatrix = new Matrix(col, row);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                newMatrix.setValues(j, i, m.getValue(i, j));
            }
        }
        return newMatrix;
    }

    /**
     * *=运算
     * */
    public Matrix mul_enual(Matrix matrix) throws Exception {
        if (m_nColumnNo != matrix.m_nRowNo) {
            throw new Exception("matrix行与列不匹配,无法进行乘法运算");
        }

        int col = matrix.m_nColumnNo;
        Matrix newMatrix = new Matrix(m_nRowNo, col);

        for (int i = 0; i < m_nRowNo; i++) {
            for (int j = 0; j < col; j++) {
                newMatrix.setValues(i, j, 0);
                for (int k = 0; k < m_nColumnNo; k++) {
                    double v1 = getValue(i, k);
                    double v2 = ((Matrix) matrix).getValue(k, j);
                    double newValue = newMatrix.getValue(i, j) + getValue(i, k)
                            * ((Matrix) matrix).getValue(k, j);
                    newMatrix.setValues(i, j, newValue);
                }
            }
        }
        Elt = newMatrix.Elt;
        m_nColumnNo = newMatrix.m_nColumnNo;
        m_nRowNo = newMatrix.m_nRowNo;
        return this;
    }

    /**
     * *=运算
     * */
    public Matrix mul_enual(double v) throws Exception {
        for (int i = 0; i < Elt.size(); i++) {
            Elt.set(i, Elt.get(i) * v);
        }
        return this;
    }

    /**
     * *运算
     * */
    public static Matrix mul(Matrix matrix1, Matrix matrix2) throws Exception {
//		return matrix1.mul_enual(matrix2);
        Matrix newMatrix = new Matrix(matrix1);
        return newMatrix.mul_enual(matrix2);
//		return newMatrix;
    }
    /**
     * !运算
     * */
    public static Matrix excalmatory_mark(Matrix matrix) throws Exception {
        Matrix newMatrix = new Matrix(matrix);
        return newMatrix.inversion();
    }
}
