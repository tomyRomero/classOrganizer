<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Class Management</title>
    <link href="main.css" rel="stylesheet" type="text/css">
</head>
<body>

<img src="books.png" alt="bookslogo">

<h1> <%= "Create Account" %></h1>

<div class="form-style">
    <form name="loginForm" method="post" action="process-signup">
        <label><span>Firstname<span class="required">*</span></span><input type="text" class="input-field" name="firstname" value="" /></label>
        <label><span>Lastname <span class="required">*</span></span><input type="text" class="input-field" name="lastname" value="" /></label>
        <label><span>Username <span class="required">*</span></span><input type="text" class="input-field" name="username" value="" /></label>
        <label><span>Password<span class="required">*</span></span><input type="password" class="input-field" name="password" value="" /></label>
        <label><span>Email<span class="required">*</span></span><input type="email" class="input-field" name="email" value="" /></label>
        <label><span>Age<span class="required">*</span></span><input type="number" class="input-field" name="age" value="" /></label>
        <input type="submit" value="Sign Up" />
    </form>
</div>
<br>
<br>
<a href="index.jsp">Home</a>
<br>
<br/>
<br>
<a href="Login.jsp">Log in</a>
</body>
</html>
