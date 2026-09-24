package com.example.entity;

/**
 * VO 类：用于封装多表查询结果
 * 当查询结果不对应单个实体类时，用 VO 封装
 *
 * 例如：SELECT empno AS p1, ename AS p2, dname AS p3 FROM emp JOIN dept ...
 */
public class Vo {

    private Integer P1;
    private String P2;
    private String P3;

    public Vo() {
    }

    public Vo(Integer p1, String p2, String p3) {
        P1 = p1;
        P2 = p2;
        P3 = p3;
    }

    public Integer getP1() { return P1; }
    public void setP1(Integer p1) { P1 = p1; }

    public String getP2() { return P2; }
    public void setP2(String p2) { P2 = p2; }

    public String getP3() { return P3; }
    public void setP3(String p3) { P3 = p3; }

    @Override
    public String toString() {
        return "Vo{" +
                "P1=" + P1 +
                ", P2='" + P2 + '\'' +
                ", P3='" + P3 + '\'' +
                '}';
    }
}
