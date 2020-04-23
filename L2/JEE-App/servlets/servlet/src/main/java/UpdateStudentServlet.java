import ejb.StudentEntity;

import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UpdateStudentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // read the parameters
        int id = Integer.parseUnsignedInt(request.getParameter("id"));
        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseUnsignedInt(request.getParameter("varsta"));

        // prepare EntityManager
        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        EntityManager em = factory.createEntityManager();

        // start transaction
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        // update information about student into database
        TypedQuery<StudentEntity> query = em.createQuery("UPDATE StudentEntity student " +
                        "SET student.nume = :nume, student.prenume = :prenume, student.varsta = :varsta " +
                        "WHERE student.id = :id",
                        StudentEntity.class);
        query.setParameter("id", id);
        query.setParameter("nume", nume);
        query.setParameter("prenume", prenume);
        query.setParameter("varsta", varsta);
        query.executeUpdate();

        // commit the transaction
        transaction.commit();

        // close EntityManager
        em.close();
        factory.close();

        // send the response to client
        response.setContentType("text/html");
        response.getWriter().print("<h3>Operatie reusita</h3><br />Informatiile au fost actualizate in baza de data." +
                "<br /><br /><a href='./fetch-student-list'>Inapoi la lista de studenti</a>" +
                "<br /><a href='./'>Inapoi la meniul principal</a>");
    }
}
