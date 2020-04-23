<html xmlns:jsp="http://java.sun.com/JSP/Page">
    <head>
        <title>Formular curs</title>
        <meta charset="UTF-8" />
    </head>
    <body>
        <h3>Formular curs</h3>
        Introduceti datele despre curs:
        <form action="./process-course" method="post">
            Nume Curs: <input type="text" name="nume" required />
            <br />
            Nume Profesor Titular: <input type="text" name="titular" required />
            <br />
            Numar Credite: <input type="number" name="credite" required />
            <br />
            <br />
            <button type="submit" name="submit">Adauga</button>
        </form>
        <br />
        <a href='./'>Inapoi la meniul principal</a>
    </body>
</html>