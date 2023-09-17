<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Class Management</title>
    <link href="main.css" rel="stylesheet" type="text/css">
</head>
<body>
<img src="books.png" alt="bookslogo">
<div class="form-style">
<h1>Login</h1>
    <form method="post" action="process-login">
        <label><span>Username <span class="required">*</span></span><input type="text" class="input-field" name="username" value="" /></label>
        <label><span>Password<span class="required">*</span></span><input type="password" class="input-field" name="password" value="" /></label>
        <input type="submit" value="Login" />
    </form>
</div>
<br>
<br>
<a href="index.jsp">Home</a>
<br>
<br>
<br>
<a href="SignUp.jsp">Sign Up</a>
</body>
</html>
