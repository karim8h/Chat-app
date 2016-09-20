<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        
    </head>
    
    <body>
        <h1 style="text-align:center;">Login</h1>
        <h5 style="color:red">${message}</h5>
        <form action="/ChatApp/login" method="POST">
            <p>User name</p>
            <input name="name" type="text" />
            <p>Password</p>
            <input name="password" type="password" />
            <br/>
            <input  type="submit" value="Login" />
        </form>
        <br />
        <p>don't have an account? <a href="/ChatApp/signup">signup</a> with a new one</p>
    </body>
    
</html>