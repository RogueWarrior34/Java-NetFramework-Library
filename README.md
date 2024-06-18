---

# Java NetFramework Library

The Java NetFramework Library is a robust framework designed to facilitate efficient communication between server and client applications over TCP/IP using JSON for data serialization. It simplifies remote method invocation and network data handling in Java-based networked environments.

## Features

- **Annotation-Driven Approach**: Utilizes `@Path` annotations to map methods and classes to specific service paths for remote invocation.
  
- **JSON Serialization**: Utilizes `JSONUtil` for seamless conversion of Java objects to JSON and vice versa, ensuring interoperability and ease of data exchange.

- **Exception Handling**: Supports custom exceptions (`BankingException` and `NetworkException`) for error handling in both server and client applications.

- **Multithreading**: Handles multiple client requests concurrently through multithreaded `RequestProcessor` instances in the server.

## Components

### Server-Side Components

#### NFrameworkServer

The `NFrameworkServer` class acts as the server-side core component:

- **Service Registration**: Registers classes containing methods annotated with `@Path` using `registerClass`.
  
- **Request Handling**: Listens for client connections on port 5500 and delegates request processing to `RequestProcessor` instances.
  
- **Dynamic Method Invocation**: Reflectively invokes registered methods based on received service paths.

#### RequestProcessor

The `RequestProcessor` class manages individual client requests:

- **Asynchronous Handling**: Processes client requests in separate threads for concurrent request handling.
  
- **Request Parsing**: Parses incoming request data into `Request` objects and invokes corresponding methods.
  
- **Response Serialization**: Serializes method responses into JSON `Response` objects for transmission back to clients.

#### TCPService

The `TCPService` class stores metadata about registered service methods:

- **Attributes**: Holds references to the class (`c`) and method (`method`) annotated with `@Path`, along with the combined service `path`.

### Client-Side Component

#### NFrameworkClient

The `NFrameworkClient` class enables client applications to interact with the server:

- **Remote Method Execution**: Executes remote methods identified by service paths and sends serialized request data to the server.
  
- **Response Handling**: Receives and parses JSON responses from the server, handling success and failure scenarios.
  
- **Exception Propagation**: Throws custom exceptions (`BankingException` and `NetworkException`) to handle server-side errors in client applications.

## Example: Banking Application

### Server (Bank.java)

```java
package com.rw.machines.nframework.server;

import com.rw.machines.nframework.server.annotations.*;

@Path("/banking")
public class Bank {

    @Path("/branchName")
    public String getBranchName(String city) throws BankingException {
        if (city.equals("Ujjain")) {
            System.out.println("Uj");
            return "Freeganj";
        }
        if (city.equals("Mumbai")) {
            System.out.println("MUm");
            return "Colaba";
        }
        System.out.println("no");
        return "No branch in that city";
    }

    public static void main(String[] args) {
        NFrameworkServer server = new NFrameworkServer();
        server.registerClass(Bank.class);
        server.start();
    }
}
```

### Client (BankUI.java)

```java
package com.rw.machines.nframework.client;

public class BankUI {

    public static void main(String[] args) {
        NFrameworkClient client = new NFrameworkClient();
        try {
            String branchName = (String) client.execute("/banking/branchName", args[0]);
            System.out.println("Branch Name: " + branchName);
        } catch (Throwable t) {
            System.out.println("Error: " + t.getMessage());
        }
    }
}
```

### How to Use

1. **Server Setup**:
   - Implement server-side classes with methods annotated with `@Path`.
   - Register classes with `NFrameworkServer` using `registerClass`.
   - Start the server with `NFrameworkServer.start()`.

2. **Client Usage**:
   - Instantiate `NFrameworkClient`.
   - Execute remote methods using `client.execute(servicePath, arguments)`.

3. **Exception Handling**:
   - Define custom exceptions (`BankingException` and `NetworkException`) for specific error scenarios.
   - Catch and handle exceptions in client applications for robust error management.

## Installation and Setup

This project has been compiled using Gradle and follows the proper folder structure:

- **Folder Structure**:
  ```
  ├── server/
  │   ├── build.gradle       # Gradle build script for server component
  │   ├── src/
  │   │   ├── main/
  │   │   │   ├── java/
  │   │   │   │   └── com/
  │   │   │   │       └── rw/
  │   │   │   │           └── machines/
  │   │   │   │               └── nframework/
  │   │   │   │                   ├── server/
  │   │   │   │                   │   ├── NFrameworkServer.java
  │   │   │   │                   │   ├── RequestProcessor.java
  │   │   │   │                   │   ├── TCPService.java
  │   │   │   │                   └── annotations/
  │   │   │   │                       └── Path.java
  ├── client/
  │   ├── build.gradle       # Gradle build script for client component
  │   ├── src/
  │   │   ├── main/
  │   │   │   ├── java/
  │   │   │   │   └── com/
  │   │   │   │       └── rw/
  │   │   │   │           └── machines/
  │   │   │   │               └── nframework/
  │   │   │   │                   └── client/
  │   │   │   │                       └── NFrameworkClient.java
  ├── common/
  │   ├── build.gradle       # Gradle build script for common component
  │   ├── src/
  │   │   ├── main/
  │   │   │   ├── java/
  │   │   │   │   └── com/
  │   │   │   │       └── rw/
  │   │   │   │           └── machines/
  │   │   │   │               └── nframework/
  │   │   │   │                   └── common/
  │   │   │   │                       ├── JSONUtil.java
  │   │   │   │                       ├── Request.java
  │   │   │   │                       ├── Response.java
  │   │   │   │                       └── exceptions/
  │   │   │   │                           └── NetworkException.java
  ├── README.md              # Project README with usage instructions and details
  └── LICENSE                # License file for the project
```

Each component (`server`, `client`, `common`) has its own dedicated directory structure and Gradle build script (`build.gradle`). This modular approach allows for easier maintenance, testing, and scalability of the Java NetFramework Library.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
