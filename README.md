# Batchy - Batching for HTTP requests
Batchy is a protocol for batching HTTP request - it allows you to send multiple HTTP requests over a single plain HTTP/1.x
It can provide significant performance improvements especially in big latency environments and high load systems

Reference server implementation is done in Java, while the protocol is easy enough to adopt it to any http client framework

# Example 

Sample request
```
POST http://localhost:9966/petclinic/batchy HTTP/1.1
Host: localhost:9966
Connection: keep-alive
Content-Length: 323
Pragma: no-cache
Cache-Control: no-cache
Origin: http://localhost:9966
User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36
Content-Type: multipart/mixed; boundary=foo
Accept: */*
Referer: http://localhost:9966/petclinic/
Accept-Encoding: gzip, deflate
Accept-Language: en,ru;q=0.8

--foo
Content-Type: application/http

POST /petclinic/mockServlet/2
Content-Type: application/json
Content-Length: part_content_length
If-Match: "etag/sheep"

{
  "animalName": "sheep",
  "animalAge": "5"
  "peltColor": "green"
}
--foo
Content-Type: application/http

GET /petclinic/mockServlet/1

--foo--
```

Sample response:
```
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Transfer-Encoding: chunked
Date: Mon, 21 Sep 2015 22:13:21 GMT

4b

--foo
Content-Type: application/http

HTTP/1.1 200
foo: a
bar: b


e
Hello World!

4f

--foo
Content-Type: application/http

HTTP/1.1 200
baz: 3
blabla: 42
```

# Install Java Servlet

Installing Batchy to your servlet is as simple as adding two tags to web.xml file:

```xml
    <servlet>
        <servlet-name>batchy</servlet-name>
        <servlet-class>com.github.bedrin.batchy.BatchyServlet</servlet-class>
    </servlet>

    <servlet-mapping>
        <servlet-name>batchy</servlet-name>
        <url-pattern>/batchy</url-pattern>
    </servlet-mapping>
```
