package com.rw.machines.nframework.common;

import com.google.gson.*;
public class JSONUtil
{
private JSONUtil(){}
public static String toJson(java.io.Serializable serializable)
{
try
{
Gson gson=new Gson();
return gson.toJson(serializable);
}catch(Exception e)
{
return "{}";
}
}
//how to use
//Bulb b=JSONUtil.fromJSON(someString,Bulb.class)
public static <T> T fromJson(String jsonString,Class<T> c)
{
try
{
Gson gson=new Gson();
return gson.fromJson(jsonString,c);
}catch(Exception e)
{
return null;
}
}
}