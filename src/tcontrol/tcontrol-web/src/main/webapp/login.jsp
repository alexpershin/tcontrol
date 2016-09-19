<%-- 
    Document   : login
    Created on : Apr 17, 2016, 1:17:49 PM
    Author     : alexey
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Login please!</h1>
        <form name="loginform" action="" method="post">
            <table align="left" border="0" cellspacing="0" cellpadding="3">
                <tr>
                    <td>Username:</td>
                    <td><input type="text" name="user" maxlength="30"></td>
                </tr>
                <tr>
                    <td>Password:</td>
                    <td><input type="password" name="pass" maxlength="30"></td>
                </tr>
                <tr>
                    <td colspan="2" align="left"><input type="checkbox" name="remember"><font size="2">Remember Me</font></td>
                </tr>
                <tr>
                    <td colspan="2" align="right"><input type="submit" name="submit" value="Login"></td>
                </tr>
            </table> 
        </form>
    </form>
    <%
        String errorDescription = (String) request.getAttribute("simpleShiroApplicationLoginFailure");
        if (errorDescription != null) {
    %>
    Login attempt was unsuccessful: <%=errorDescription%>
    <%
        }
    %>
</body>
</html>
