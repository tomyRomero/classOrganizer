<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.classorganizer.Html" %>
<html>
<head>
    <title>Remove Class</title>
    <link href="main.css" rel="stylesheet" type="text/css">
</head>
<body>
<%@ include file="/WEB-INF/message.jspf" %>
<img src="books.png" alt="bookslogo">
<h1>Remove Class</h1>
<h2>User: ${Html.escape(name)}</h2>

<div class="form-style">
    <form name="loginForm" method="post" action="process-remove-class">
        <label><span>Classname<span class="required">*</span></span><input type="text" class="input-field" name="class-name" value="" required /></label>
        <br>
        <br>
        <label><span>CRN<span class="required">*</span></span><input type="number" class="input-field" name="class crn" value="" required/></label>
        <br>
        <br>
        <input type="submit" value="Remove Class" />
    </form>
</div>
<a href="Landing.jsp">Home</a>
</body>
</html>
