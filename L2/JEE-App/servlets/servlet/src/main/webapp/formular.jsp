<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Formular student</title>
        <meta charset="UTF-8" />
    </head>
    <body>
        <h3>Formular student</h3>
        Introduceti datele despre student:
        <form action="./process-student" method="post">
            Nume: <input type="text" name="nume" required />
            <br />
            Prenume: <input type="text" name="prenume" required />
            <br />
            Varsta: <input type="number" name="varsta" required />
            <br />
            <br />
            <button type="submit" name="submit">Trimite</button>
        </form>
        <br />
        <a href='./'>Inapoi la meniul principal</a>
    </body>
</html>