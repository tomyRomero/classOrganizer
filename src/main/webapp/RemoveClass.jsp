<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.classorganizer.Html" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Remove Class</title>
    <link href="main.css" rel="stylesheet" type="text/css">
</head>
<body>
<header class="bar">
    <span class="brand">Class Organizer</span>
    <nav><a href="Landing.jsp">Home</a></nav>
</header>
<main>
<%@ include file="/WEB-INF/message.jspf" %>
<img src="books.png" alt="bookslogo">
<h1>Remove Class</h1>
<h2>User: ${Html.escape(name)}</h2>
<div class="form-style">
    <form name="loginForm" method="post" action="process-remove-class">
        <label><span>Classname<span class="required">*</span></span><input type="text" class="input-field" name="class-name" value="" required /></label>
        <label><span>CRN<span class="required">*</span></span><input type="number" class="input-field" name="class crn" value="" required/></label>
        <input type="submit" value="Remove Class" />
    </form>
</div>
</main>
</body>
</html>
