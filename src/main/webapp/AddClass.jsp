<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.classorganizer.Html" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Add Class</title>
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
<h1>Add Class</h1>
<h2>User: ${Html.escape(name)}</h2>
<div class="form-style">
    <form name="add-class-form" method="post" action="process-class">
        <label><span>Classname<span class="required">*</span></span><input type="text" class="input-field" name="class-name" value="" required/></label>
        <label><span>ClassSubject</span><select name="class-subject" class="select-field" required>
            <option value="Math">Math</option>
            <option value="Humanities">Humanities</option>
            <option value="SocialScience">Social Science</option>
            <option value="PhysicalEducation">Physical Education</option>
            <option value="Science">Science</option>
        </select></label>
        <label><span>Time<span class="required">*</span></span><input type="time" class="input-field" name="class time" value="" required/></label>
        <fieldset class="days">
            <legend>Days</legend>
            <label><input type="checkbox" name="Monday" value="Monday" checked/> Monday</label>
            <label><input type="checkbox" name="Tuesday" value="Tuesday"/> Tuesday</label>
            <label><input type="checkbox" name="Wednesday" value="Wednesday"/> Wednesday</label>
            <label><input type="checkbox" name="Thursday" value="Thursday"/> Thursday</label>
            <label><input type="checkbox" name="Friday" value="Friday"/> Friday</label>
            <label><input type="checkbox" name="Saturday" value="Saturday"/> Saturday</label>
        </fieldset>
        <label><span>Professor<span class="required">*</span></span><input type="text" class="input-field" name="class professor" value="" required/></label>
        <label><span>CRN<span class="required">*</span></span><input type="number" class="input-field" name="class crn" value="" required/></label>
        <label><span>Grade<span class="required">*</span></span><input type="number" class="input-field" name="class grade" value="" required/></label>
        <label><span>Credits<span class="required">*</span></span><input type="number" class="input-field" name="class credit" value="" required/></label>
        <input type="submit" value="Add Class" />
    </form>
</div>
</main>
</body>
</html>
