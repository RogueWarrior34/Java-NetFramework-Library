package com.rw.machines.nframework.client;
import java.nio.charset.*;
import java.net.*;
import java.io.*;
import com.rw.machines.nframework.common.*;
import com.rw.machines.nframework.common.exceptions.*;


public class NFrameworkClient
{
public Object execute(String servicePath,Object ...arguments) throws Throwable
{
try
{
Request request=new Request();
request.setServicePath(servicePath);
request.setArguments(arguments);
String requestJSONString=JSONUtil.toJson(request);
byte objectBytes[];
objectBytes=requestJSONString.getBytes(StandardCharsets.UTF_8);
int requestLength=objectBytes.length;
byte header[]=new byte[1024];
int x;
int i;
i=1023;
x=requestLength;
while(x>0)
{
header[i]=(byte)(x%10);
x=x/10;
i--;
}
Socket socket=new Socket("localhost",5500);
OutputStream os=socket.getOutputStream();
os.write(header,0,1024);
os.flush();
System.out.println("header has been sent: "+requestLength);
InputStream is=socket.getInputStream();
byte ack[]=new byte[1];
int bytesReadCount;
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
System.out.println("ack has been received");
int bytesToSend=requestLength;
int chunkSize=1024;
int j=0;
while(j<bytesToSend)
{
if((bytesToSend-j)<chunkSize) chunkSize=bytesToSend-j;
os.write(objectBytes,j,chunkSize);
os.flush();
j=j+chunkSize;
}
System.out.println("Request has been sent");
while(true)
{
bytesReadCount=is.read(ack);
if(bytesReadCount==-1) continue;
break;
}
System.out.println("ack has been received");
int bytesToReceive=1024;
byte tmp[]=new byte[1024];
int k;
i=0;
j=0;
while(j<bytesToReceive)
{
bytesReadCount=is.read(tmp);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesToReceive;k++)
{
header[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
}
int responseLength=0;
i=1;
j=1023;
while(j>=0)
{
responseLength=responseLength+(header[j]*i);
j--;
i=i*10;
}
System.out.println("Header received:"+responseLength);
ack[0]=1;
os.write(ack);
os.flush();
byte response[]=new byte[responseLength];
bytesToReceive=responseLength;
i=0;
j=0;
while(j<bytesToReceive)
{
bytesReadCount=is.read(tmp);
System.out.println(bytesReadCount);
if(bytesReadCount==-1) continue;
for(k=0;k<bytesToReceive;k++)
{
response[i]=tmp[k];
i++;
}
j=j+bytesReadCount;
System.out.println(j);
}
ack[0]=1;
os.write(ack);
os.flush();
System.out.println("Response has been received");
socket.close();
String responseJSONString=new String(response,StandardCharsets.UTF_8);
Response responseObject=JSONUtil.fromJson(responseJSONString,Response.class);
if(responseObject.getSuccess()==true)
{
return responseObject.getResult();
}
else
{
throw (Throwable)responseObject.getException();
}
}catch(Exception e)
{
System.out.println(e);
}
return null;
}
}

