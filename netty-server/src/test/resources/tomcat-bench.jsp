<%--
ab -n 100000 -c 1000 -q http://172.27.234.152:8080/test.jsp
--%><% long start = System.nanoTime(); %><%!
java.util.concurrent.atomic.AtomicLong accessCount = new java.util.concurrent.atomic.AtomicLong();
java.util.concurrent.atomic.AtomicLong costTime = new java.util.concurrent.atomic.AtomicLong();
%>
<html>
<body bgcolor="white">
<h1> Request Information </h1>
<font size="4">
Access Count: <%= accessCount.addAndGet(1) %>
<br>
JSP Request Method: <%= (request.getMethod()) %>
<br>
Request URI: <%= (request.getRequestURI()) %>
<br>
Request Protocol: <%= (request.getProtocol()) %>
<br>
Servlet path: <%= (request.getServletPath()) %>
<br>
Path info: <%= (request.getPathInfo()) %>
<br>
Query string: <%= (request.getQueryString()) %>
<br>
Content length: <%= request.getContentLength() %>
<br>
Content type: <%= (request.getContentType()) %>
<br>
Server name: <%= (request.getServerName()) %>
<br>
Server port: <%= request.getServerPort() %>
<br>
<hr>
Average Execute Cost Time: <%=(costTime.addAndGet(System.nanoTime()-start)/accessCount.get())/1000000%> ms 
<br>
Total Cost Time: <%=costTime.get()/1000000%> ms 
<br>
<hr>
</font>
</body>
</html>