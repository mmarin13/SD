<html>
    <body>
        <h2>Hello World!</h2>
        <br/>
        <p>
            <a href="./hello">Acces la primul servlet</a>
        </p>
        <p>
            <a href="./formular.jsp">Formular adaugare student</a>
        </p>
        <p>
            Vizualizare student:
            <br />
            <form action="./read-student" method="get">
                &nbsp;&nbsp;&nbsp;&nbsp;Nr. matricol: <input type="number" name="nr_matricol" required />
                <button type="submit" name"submit">Cauta</button>
            </form>
        </p>
    </body>
</html>
