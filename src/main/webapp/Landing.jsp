
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.classorganizer.Html" %>
<html>
<head>
    <title>Landing</title>
    <link href="main.css" rel="stylesheet" type="text/css">
</head>
<body>
<%@ include file="/WEB-INF/message.jspf" %>
<img src="books.png" alt="bookslogo">
<h1 class="heading"> Welcome User: ${Html.escape(name)}</h1>
<br>
<br>
<br>
<a href="AddClass.jsp">Add Class</a>
<br>
<br>
<br>
<a href="RemoveClass.jsp">Remove Class</a>
<br>
<br>
<br>
<a href="table">View Classes</a>
<br>
<br>
<br>
<a href="account">View Account</a>
<br>
<br>
<br>
<a href="logout">Log Out</a>
<br>
<br>
<br>
</body>
</html>
