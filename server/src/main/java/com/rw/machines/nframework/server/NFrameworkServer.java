package com.rw.machines.nframework.server;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import com.rw.machines.nframework.server.annotations.*;


public class NFrameworkServer
{
private ServerSocket serverSocket;
private Set<Class> tcpNetworkServiceClasses;
private Map<String,TCPService> services;
public NFrameworkServer()
{
tcpNetworkServiceClasses=new HashSet<>();
services=new HashMap<>();
}

public void registerClass(Class c)
{
Path pathOnType;
Path pathOnMethod;
Method methods[];
String fullPath;
pathOnType=(Path)c.getAnnotation(Path.class);
if(pathOnType==null) return;
methods=c.getMethods();
int methodwithPathAnnotationCount=0;
for(Method method:methods)
{
pathOnMethod=(Path)method.getAnnotation(Path.class);
if(pathOnMethod==null) continue;
methodwithPathAnnotationCount++;
fullPath=pathOnType.value()+pathOnMethod.value();
TCPService tcpService=new TCPService();
tcpService.c=c;
tcpService.method=method;
tcpService.path=fullPath;
services.put(fullPath,tcpService);
}
if(methodwithPathAnnotationCount>0)
{
tcpNetworkServiceClasses.add(c);
}
}

public TCPService getTCPNetworkService(String path)
{
TCPService tcpService=services.get(path);
if(tcpService==null)
{
return null;
}
return tcpService;
}


public void start()
{
try
{
serverSocket=new ServerSocket(5500);
Socket socket;
RequestProcessor requestProcessor;
while(true)
{
System.out.println("Server is ready to accept requests at 5500");
socket=serverSocket.accept();
requestProcessor=new RequestProcessor(this,socket);
}
}catch(Exception e)
{
System.out.println(e);
}
}
}