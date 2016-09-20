<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <style>
            #modalWindow{
                position:fixed;
                display: none;
                top: 0;
                left: 0;
                height: 100%;
                width: 100%;
                z-index: 1;
                background-color: rgba(0,0,0,0.4);
            }
            #modalContents{
                background-color: white;
                margin: auto;
                margin-top: 10%;
                width: 400px;
                height: 100px;
                
            }
            #btnOk{
                margin-left: 10%;
                width: 20%;
               // background-color: yellow;
            }
            #btnCancel{
                margin-left: 30%;
                width: 20%;
            }
            #modalMessage{
                padding-top: 5%;
                height: 30%;
                overflow-y: auto;
                text-align: center;
            }

        </style>
    </head>
    
    <body>
        <script>
        
        $(document).ready(function(){
            makeLongPoll();
            refreshStatus();
        });
        
        function makeLongPoll() {
            $.ajax({
                url : "/ChatApp/hold_me",
                method : "PUT",
                timeout : 0,
                dataType : "json",
                success : function(data) {
                              if (makeDecision(data)) {
                                  makeLongPoll();
                              }
                          },
                error : function(a, b, c) {
                            alert("polling error : " + b + " : " + c);
                        }
            });
        }

        function makeReq(jsonObject, requestURL) {
            $.ajax({
                url : requestURL,
                method : "POST",
                headers : {"Access-Control-Allow-Origin" : "*"},
          //      contentType : "application/json",
                timeout : 1000,
                //data : JSON.stringify(jsonObject),
                data : jsonObject,
                success : function(data) {
                                //alert("request success");
                                console.log("request success");
                          },
                error : function(a, b, c) {
                            alert("request error : " + b + " : " + c);
                           // console.log("request error : " + b + " : " + c");
                        }
            });
        }
        
        function sendChatRequest(name) {
            makeReq({"name" : name}, "/ChatApp/chat/req");
        }
            
            
        
        function makeDecision(data) {
            if (data["message"] == "chat:request") {
                displayModal(data, function() {acceptChatRequest(data);}, 
                    function() {refuseChatRequest(data);},
                        data['targetName'] + ", wants to chat with you.");
                return true;
            }
            else if (data["message"] == "chat:refusal") {
                displayModal(data, doNothing, 
                    doNothing, data['targetName'] + ", refused your chat request.");
                return true;
            }
            else if (data["message"] == "chat:approval") {
                displayModal(data, doNothing, 
                        doNothing, data['targetName'] + ", accepted your chat request.");
                return true;
            }
            else if (data["message"] == "redirect") {
                gotoURL(data["actionURL"]);
                return false;    
            }
            else if (data["message"] == "remove_user") {
                removeUser(data["targetName"]);
                return true;
            }
            else if (data["message"] == "add_user") {
                addUser(data["targetName"]);
                return true;
            }
            return true;
        }
        
        function displayModal(data, okFunction, cancelFunction, modalMessage) {
            document.getElementById("btnOk").onclick = okFunction;
            document.getElementById("btnCancel").onclick = cancelFunction;
            document.getElementById("modalMessage").innerHTML = modalMessage;
            document.getElementById("modalWindow").style.display = "block";
        }
        
        function gotoURL(url) {
            window.location = url;
        }
        
        function doNothing() {
            document.getElementById("modalWindow").style.display = "none";
        }
        
        function acceptChatRequest(data) {
            makeReq({
            	name : data["targetName"],
                response : true,
            }, data["actionURL"]);
            document.getElementById("modalWindow").style.display = "none";
        }
        function refuseChatRequest(data) {
            makeReq({
            	name : data["targetName"],
                response : false,
            }, data["actionURL"]);
            document.getElementById("modalWindow").style.display = "none";
        }
        
        function removeUser(name) {
        	if (name === "${userName}") {
        		return;
        	}
            var table = document.getElementById("onlineUsers");
            for (var i = 1; i < table.rows.length; ++i) {
                var row = table.rows[i];
                if (row.cells[0].innerHTML === name) {
                    table.deleteRow(i);
                }
            }
        }
        
        function addUser(name) {
        	if (name === "${userName}") {
        		return;
        	}
            var table = document.getElementById("onlineUsers");
            for (var i = 1; i < table.rows.length; ++i) {
                var row = table.rows[i];
                if (row.cells[0].innerHTML === name) {
                    return;
                }
            }
            var newRow = table.insertRow();
            
            newRow.insertCell().innerHTML = name;
            
            var link = document.createElement("a");
            link.setAttribute("href", "javascript:void(0)");
            link.setAttribute("onclick", "sendChatRequest('" + name + "')");
            link.innerHTML = "chat";
            
            newRow.insertCell().appendChild(link);
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
        
        <table border="0" style="width: 50%" id="onlineUsers" >
		<tr><th>Online users</th></tr>
		<c:forEach items="${allUsers}" var="onlineUser">
			<tr>
				<td>${onlineUser.getName()}</td>
				<td><a href="javascript:void(0)" onclick='sendChatRequest("${onlineUser.getName()}")' >chat</a></td>
			</tr>
		</c:forEach>
	</table>
        
        <div id="modalWindow">
            <div id="modalContents">
                <p id="modalMessage"></p>
                <button id="btnOk" onclick="modalOk()">ok</button>
                <button id="btnCancel" onclick="modalCancel()">cancel</button>
            </div>
        </div>
        
    </body>
    
</html>