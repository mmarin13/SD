<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Actualizare curs</title>
        <meta charset="UTF-8" />
    </head>
    <body>
        <h3>Actualizare curs</h3>
        Actualizati datele despre curs:
        <form action="./update-course" method="post">
            <input type="hidden" name="id" value='<%= request.getParameter("id") %>' />
            Nume: <input type="text" name="nume" value='<%= request.getParameter("nume") %>' />
            <br />
            Prenume: <input type="text" name="titular" value='<%= request.getParameter("titular") %>' />
            <br />
            Varsta: <input type="number" name="credite" value='<%= request.getParameter("credite") %>' />
            <br />
            <br />
            <button type="submit" name="submit">Actualizeaza</button>
        </form>
        <br />
        <a href='./fetch-course-list'>Inapoi la lista de cursuri</a>
        <br />
        <a href='./'>Inapoi la meniul principal</a>
    </body>
</html>