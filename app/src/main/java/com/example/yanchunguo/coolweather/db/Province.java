package com.example.yanchunguo.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by yanchun.guo on 2017/9/29.
 */
//省份
public class Province extends DataSupport {
    private int id;
    private String provinceName;
    private String provinceCode;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getProvinceName(){
        return provinceName;
    }
    public void setProvinceName(String provinceName){
        this.provinceName = provinceName;
    }
    public String getProvinceCode(){
        return provinceCode;
    }
    public void setProvinceCode(String provinceCode){
        this.provinceCode = provinceCode;
    }
}
