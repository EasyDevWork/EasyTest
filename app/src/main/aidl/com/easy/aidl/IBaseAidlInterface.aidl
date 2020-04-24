package com.easy.aidl;

//文件以及包名要跟服务端一致
interface IBaseAidlInterface {

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,double aDouble, String aString);

    int getInt();
    long getLong();
    boolean getBoolean();
    float getFloat();
    double getDouble();
    String getString();
}
