<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Informatii student</title>
    </head>
    <body>
        <h3>Informatii student</h3>

        <!-- populare bean cu informatii din cererea HTTP -->
        <jsp:useBean id="studentBean" class="beans.StudentBean" />
        <jsp:setProperty name="studentBean" property="nrMatricol" value='<%=request.getAttribute("nr_matricol") %>' />
        <jsp:setProperty name="studentBean" property="nume" value='<%=request.getAttribute("nume") %>' />
        <jsp:setProperty name="studentBean" property="prenume" value='<%=request.getAttribute("prenume") %>' />
        <jsp:setProperty name="studentBean" property="varsta" value='<%=request.getAttribute("varsta") %>' />

        <!-- folosirea bean-ului pentru afisarea informatiilor -->
        <ul type="bullet">
            <li>Nr. matricol: <jsp:getProperty name="studentBean" property="nrMatricol" /></li>
            <li>Nume: <jsp:getProperty name="studentBean" property="nume" /></li>
            <li>Prenume: <jsp:getProperty name="studentBean" property="prenume" /></li>
            <li>Varsta: <jsp:getProperty name="studentBean" property="varsta" /></li>
            <li>Anul nasterii: <%
                 Object anNastere = request.getAttribute("anNastere");
                 if (null != anNastere) {
                  out.print(anNastere);
                 } else {
                  out.print("necunoscut");
                 }
            %></li>
        </ul>

        <!-- formular de actualizare date student -->
        <br />
        <h3>Actualizare informatii student</h3>
        <form action="./update-student" method="post">
            <input type="hidden" name="old_nr_matricol" value='<jsp:getProperty name="studentBean" property="nrMatricol" />' />
            Nr. matricol: <input type="number" name="nr_matricol" value='<jsp:getProperty name="studentBean" property="nrMatricol" />' />
            <br />
            Nume: <input type="text" name="nume" value='<jsp:getProperty name="studentBean" property="nume" />' />
            <br />
            Prenume: <input type="text" name="prenume" value='<jsp:getProperty name="studentBean" property="prenume" />' />
            <br />
            Varsta: <input type="number" name="varsta" value='<jsp:getProperty name="studentBean" property="varsta" />' />
            <br />
            <br />
            <button type="submit" name="submit">Actualizare</button>
        </form>
        <br />
        <a href="./">Back</a>
    </body>
</html>