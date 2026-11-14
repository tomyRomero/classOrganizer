<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Class Management</title>
    <link href="main.css" rel="stylesheet" type="text/css">
</head>
<body>
<header class="bar">
    <span class="brand">Class Organizer</span>
    <nav><a href="index.jsp">Home</a> <a href="SignUp.jsp">Sign Up</a></nav>
</header>
<main>
<%@ include file="/WEB-INF/message.jspf" %>
<img src="books.png" alt="bookslogo">
<h1>Login</h1>
<div class="form-style">
    <form method="post" action="process-login">
        <label><span>Username <span class="required">*</span></span><input type="text" class="input-field" name="username" value="" /></label>
        <label><span>Password<span class="required">*</span></span><input type="password" class="input-field" name="password" value="" /></label>
        <input type="submit" value="Login" />
    </form>
</div>
</main>
</body>
</html>
