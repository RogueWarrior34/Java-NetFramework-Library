package com.rw.machines.nframework.server;
import java.net.*;
import java.io.*;
import com.rw.machines.nframework.common.*;
import java.lang.reflect.*;
import java.nio.charset.*;

class RequestProcessor extends Thread
{
private NFrameworkServer server;
private Socket socket;
public RequestProcessor(NFrameworkServer server,Socket socket)
{
this.socket=socket;
this.server=server;
start();
}

public void run()
{
try
{
InputStream is=socket.getInputStream();
OutputStream os=socket.getOutputStream();
int bytesToReceive=1024;
byte tmp[]=new byte[1024];
byte header[]=new byte[1024];
int bytesReadCount;
int i,j,k;
i=0;
j=0;
while(j<bytesToReceive)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
header[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
int requestLength=0;
i=1;
j=1023;
while(j>=0)
{
requestLength=requestLength+(header[j]*i);
i=i*10;
j--;
}
System.out.println("header has been received: "+requestLength);
byte ack[]=new byte[1];
ack[0]=1;
os.write(ack);
os.flush();
System.out.println("Ack has been sent");
byte request[]=new byte[requestLength];
bytesToReceive=requestLength;
i=0;
j=0;
while(j<bytesToReceive)
{
bytesReadCount=is.read(tmp);
System.out.println(bytesReadCount);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesReadCount;k++)
{
request[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
System.out.println("Request has been received");
ack[0]=1;
os.write(ack);
os.flush();
System.out.println("Ack has been sent");
String requestJSONString=new String(request,StandardCharsets.UTF_8);
Request requestObject=JSONUtil.fromJson(requestJSONString,Request.class);
String servicePath=requestObject.getServicePath();
TCPService tcpService=this.server.getTCPNetworkService(servicePath);
Response responseObject=new Response();
if(tcpService==null)
{
responseObject.setSuccess(false);
responseObject.setResult(null);
responseObject.setException(new RuntimeException("Invalid path: "+servicePath));
}
else
{
Class c=tcpService.c;
Method m=tcpService.method;
try
{
Object serviceObject=c.newInstance();
Object result=m.invoke(serviceObject,requestObject.getArguments());
responseObject.setResult(result);
responseObject.setSuccess(true);
responseObject.setException(null);
}catch(InstantiationException instantiationException)
{
responseObject.setResult(null);
responseObject.setSuccess(false);
responseObject.setException(new RuntimeException("Unable to create object of the service class: "+servicePath));
}
catch(IllegalAccessException illegalAccessException)
{
responseObject.setResult(null);
responseObject.setSuccess(false);
responseObject.setException(new RuntimeException("Unable to create object of the service class: "+servicePath));
}
catch(InvocationTargetException invocationTargetException)
{
responseObject.setResult(null);
responseObject.setSuccess(false);
responseObject.setException(invocationTargetException.getCause());
}
}
String responseJSONString=JSONUtil.toJson(responseObject);
byte objectByte[]=responseJSONString.getBytes(StandardCharsets.UTF_8);
int responseLength=objectByte.length;
int x;
i=1023;
x=responseLength;
header=new byte[1024];
while(x>0)
{
header[i]=(byte)(x%10);
x=x/10;
i--;
}
os.write(header,0,1024);
os.flush();
System.out.println("Response Header has been sent");
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
System.out.println("Ack has been received");
int bytesToSend=responseLength;
int chunkSize=1024;
j=0;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize=(bytesToSend-j);
os.write(objectByte,j,chunkSize);
os.flush();
j=j+chunkSize;
}
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
System.out.println("Response has been sent");
socket.close();
}catch(IOException e)
{
System.out.println(e);
}
}
}