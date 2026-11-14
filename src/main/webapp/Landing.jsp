<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.classorganizer.Html" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Landing</title>
    <link href="main.css" rel="stylesheet" type="text/css">
</head>
<body>
<header class="bar">
    <span class="brand">Class Organizer</span>
    <nav><a href="logout">Log Out</a></nav>
</header>
<main>
<%@ include file="/WEB-INF/message.jspf" %>
<img src="books.png" alt="bookslogo">
<h1 class="heading"> Welcome User: ${Html.escape(name)}</h1>
<nav class="menu">
    <a class="button" href="AddClass.jsp">Add Class</a>
    <a class="button" href="RemoveClass.jsp">Remove Class</a>
    <a class="button" href="table">View Classes</a>
    <a class="button" href="account">View Account</a>
</nav>
</main>
</body>
</html>
