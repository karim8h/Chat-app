<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    </head>
    
    <body>
    	<script>
    		function validate() {
                
                var p1 = document.getElementById("password1").value;
                var p2 = document.getElementById("password2").value;
                if (p1 === p2) {
                    document.getElementById("signupForm").submit();
                    return;
                }
                document.getElementById("failureMessage").innerHTML = "passwords donot match";
                document.getElementById("failureMessage").style.color = "red";
                
    		}
    	</script>
    	
    
    
        <h1 style="text-align:center;">New account</h1>
        <h5 style="color:red">${message}</h5>
        <form action="/ChatApp/signup" method="POST" id="signupForm">            
            <p>First name</p>
            <input name="firstName" type="text" />
            <p>Last name</p>
            <input name="lastName" type="text" />
            <p>User name</p>
            <input name="userName" type="text" />
            <br />
            <p>Password</p>
            <input id="password1" name="password" type="password" />
            <p>retype password</p>
            <input id="password2" type="password" />
            <br/>
        </form>
        <button onclick="validate()">create account</button>
        
    </body>
    
</html>