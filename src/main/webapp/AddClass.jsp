
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add Class</title>
    <link href="main.css" rel="stylesheet" type="text/css">
</head>
<body>
<img src="books.png" alt="bookslogo">
<h1>Add Class</h1>
<h1>User: ${name}</h1>
<div class="form-style">
    <form name="add-class-form" method="post" action="process-class">
        <label><span>Classname<span class="required">*</span></span><input type="text" class="input-field" name="class-name" value="" required/></label>
        <br>
        <label><span>ClassSubject</span><select name="class-subject" class="select-field" required>
            <option value="Math">Math</option>
            <option value="Humanities">Humanities</option>
            <option value="SocialScience">Social Science</option>
            <option value="PhysicalEducation">Physical Education</option>
            <option value="Science">Science</option>
        </select></label>
        <br>
        <br>
        <label><span>Time<span class="required">*</span></span><input type="time" class="input-field" name="class time" value="" required/></label>
        <br>
        <br>

        <div class="dbgOuter">
            <div class="dbgCont">
                <input type="checkbox" id="day1" class="dbgCheck" name="Monday" value="Monday" checked/>
                <label for="day1">Monday</label>
            </div>
            <div class="dbgCont">
                <input type="checkbox" id="day2" class="dbgCheck" name="Tuesday" value="Tuesday"/>
                <label for="day2">Tuesday</label>
            </div>
            <div class="dbgCont">
                <input type="checkbox" id="day3" class="dbgCheck" name="Wednesday" value="Wednesday"/>
                <label for="day3">Wednesday</label>
            </div>
            <div class="dbgCont">
                <input type="checkbox" id="day4" name="Thursday" value="Thursday" class="dbgCheck"/>
                <label for="day4">Thursday</label>
            </div>
            <div class="dbgCont">
                <input type="checkbox" id="day5"  class="dbgCheck" name="Friday" value="Friday"/>
                <label for="day5">Friday</label>
            </div>
            <div class="dbgCont">
                <input type="checkbox" id="day6"  class="dbgCheck" name="Saturday" value="Saturday" />
                <label for="day6">Saturday</label>
            </div>
        </div>

        <br>
        <label><span>Professor<span class="required">*</span></span><input type="text" class="input-field" name="class professor" value="" required/></label>
        <br>
        <br>
        <label><span>CRN<span class="required">*</span></span><input type="number" class="input-field" name="class crn" value="" required/></label>
        <br>
        <br>
        <label><span>Grade<span class="required">*</span></span><input type="number" class="input-field" name="class grade" value="" required/></label>
        <br>
        <br>
        <label><span>Credits<span class="required">*</span></span><input type="number" class="input-field" name="class credit" value="" required/></label>
        <br>
        <br>
        <input type="submit" value="Add Class" />
    </form>
</div>
<br>
<a href="Landing.jsp">Home</a>
</body>
</html>
