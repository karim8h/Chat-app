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
               // background-color: yellow;
            }
            #modalMessage{
               // background-color: yellow;
                padding-top: 5%;
                height: 30%;
                overflow-y: auto;
                text-align: center;
            }
            #onlineUsers{
            	list-style-type: none;
            	padding-left: 0px;
            }
        </style>
    </head>
    
    <body>
        <script>
        
        $(document).ready(function(){
            makeLongPoll();
        });
        
        function makeLongPoll() {
            $.ajax({
                url : "http://localhost:8080/ChatApp/hold_me?id=${id}",
                timeout : 0,
                dataType : "json",
                success : function(data) {
                              makeDecision(data);
                              makeLongPoll();
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
                    contentType : "application/json",
                    timeout : 1000,
                    data : JSON.stringify(jsonObject),
                    success : function(data) {
                                    alert("request success");
                              },
                    error : function(a, b, c) {
                                alert("request error : " + b + " : " + c);
                            }
                });
            }
            
            function makeDecision(data) {
                if (data["message"] == "chat:request") {
                    displayModal(data, function() {acceptChatRequest(data);}, 
                        function() {refuseChatRequest(data);},
                            data['senderName'] + ", wants to chat with you.");
                }
                else if (data["message"] == "chat:refusal") {
                    displayModal(data, doNothing, 
                        doNothing, data['senderName'] + ", refused your chat request.");
                        
                }
                else if (data["message"] == "chat:approval") {
                    displayModal(data, doNothing, 
                        doNothing, data['senderName'] + ", accepted your chat request.");
                }
                else if (data["message"] == "redirect") {
                    gotoURL(data["actionURL"]);
                        
                }
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
                    senderId : ${id},
                    targetId : data["senderId"],
                    senderName : "${userName}",
                    actionUrl : "none",
                    message : "chat:approval"
                }, data["actionURL"]);
                document.getElementById("modalWindow").style.display = "none";
            }
            function refuseChatRequest(data) {
                makeReq({
                    senderId : ${id},
                    targetId : data["senderId"],
                    senderName : "${userName}",
                    actionUrl : "none",
                    message : "chat:refusal"
                });
                document.getElementById("modalWindow").style.display = "none";
            }
            
        </script>
    
        <ul id="onlineUsers">
        <li><a name="user2" onclick='makeReq({
                    senderId : 1,
                    targetId : 2,
                    senderName : "none",
                    actionUrl : "none",
                    message : "none"
        }, "http://localhost:8080/ChatApp/chat/req")'>request chat with user2</a></li>
        </ul>
        
        <table border="0" style="width: 50%">
		<tr><th>Online users</th></tr>
		<c:forEach items="${allUsers}" var="user">
			<tr>
				<td>${user.getUserName()}</td>
				<td><a href="/chat?id=${user.getId()}">chat</a></td>
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