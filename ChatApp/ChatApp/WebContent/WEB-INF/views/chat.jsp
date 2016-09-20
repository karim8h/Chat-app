<!-- <%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
-->

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <style>
        	#dropButton{
        		width: 30%;
                height: 80%;
                margin-top: 2%;
        	}
        	#navBar{
        		background-color: blue;
        		position: fixed;
                top: 90%;
        		left: 0px;
        		width: 100%;
        		height: 10%;
        	}
            #inputBox{
                margin-left: 10%;
                margin-top: 2%;
                width: 20%;
            }
        </style>
        
    </head>
    
    <body>
        <script>
        
            var partnerArray = [];
            $(document).ready(function() {
                makeLongPoll();
                refreshStatus();
            	<c:forEach items="${partners}" var="partner">partnerArray.push("${partner}");</c:forEach>
            });
            
            function makeLongPoll() {
                $.ajax({
                    url : "/ChatApp/chat",
                    method : "PUT",
                    timeout : 0,
                    dataType : "json",
                    error : function (a, b, c) {alert("recieve error : " + b + " : " + c);},
                    success : function (data) {
                        if (makeDecision(data)) {
	                        makeLongPoll();	
                        }
                    }
                });
            }
            
            function makeDecision(data) {
            	if (data["actionURL"] != "none") {
                	window.location = "/ChatApp/home/${userName}";
                	return false;
            	}
            	addItemToList(data["targetName"], data["message"]);
            	return true;
            }
            
            function sendMyMessage(myMessage, targetName) {
                var jsonObject = {
                	"targetName" : targetName,
                    "message" : myMessage,
                    "actionURL" : "none"
                };
                $.ajax({
                    url : "/ChatApp/chat",
                    timeout : 1000,
                    method : 'POST',
                    data : JSON.stringify(jsonObject),
                    headers : {'Access-Control-Allow-Origin': '*'},
                    contentType : "application/json",
                    error : function (a, b, c) {alert("sending error : " + b + " : " + c);},
                    //success : function (data) {alert("sending success");}
                });
            }
            
            function sendToAllUsers(myMessage) {
            	for (var i = 0; i < partnerArray.length; i++) {
            		sendMyMessage(myMessage, partnerArray[i]);
            	}
            }
        
            function eventHandler(event) {
                if (event.keyCode == 13) {
                    var inputBox = document.getElementById("inputBox");
                    var message = inputBox.value;
                    if (message.length == 0) {
                        return;
                    }
                    inputBox.value = "";
                    sendToAllUsers(message);
                }     
            }
            
            function addItemToList(userName, message) {
                var ul = document.getElementById("list");
                var li = document.createElement("li");
                li.appendChild(document.createTextNode(userName + " : " + message));
                ul.appendChild(li);
                if (userName === "${userName}") {
                	li.style.color = "red";
                }
            }
            
            function dropChat() {
            	window.location = "/ChatApp/chat/drop";
            }
            
            function refreshStatus() {
                
                var refresh = function() {
    	            $.ajax({
    	                url : "/ChatApp/refresh_status",
    	                method : "POST",
    	              //  headers : {"Access-Control-Allow-Origin" : "*"},
    	              //  data : {"id":${user.getId()}},
    	                timeout : 1000,
    	                //success : function(data) {alert("refresh success");},
    	                //error : function(a,b,c) {alert("refresh error : " + a + " : " + b + " : " + c);}
    	                error : function(data) {console.log("refresh error");}
    	            });
    	        };
            	
    	       	refresh();
                setInterval(refresh, 1000);
            }
        
        </script>
    	
    	<div id="navBar">
            <table width=100%>
                <tr>
                    <td>
                        <input id="inputBox" type="text" onkeypress="eventHandler(event)">
                    </td>
                    <td>
                        <button id="dropButton" onclick="dropChat()">Drop Chat</button>
                    </td>
                </tr>
            </table>
    	</div>
    	
        <div style="margin-top: 5%;font-size:20px;">
            <ul id="list" style="list-style-type: none;">
            </ul>
         </div>
    </body>
    
</html>