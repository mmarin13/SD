import ejb.StudentEntity;

import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteStudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // read the ID parameter
        int id = Integer.parseUnsignedInt(request.getParameter("id"));

        // prepare EntityManager
        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        EntityManager em = factory.createEntityManager();

        // start transaction
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        // delete the student with specified id from database
        TypedQuery<StudentEntity> query = em.createQuery("DELETE FROM StudentEntity student WHERE student.id = :id",
                StudentEntity.class);
        query.setParameter("id", id);
        query.executeUpdate();

        // commit the transaction
        transaction.commit();

        // close EntityManager
        em.close();
        factory.close();

        // send the response to client
        response.setContentType("text/html");
        response.getWriter().print("<h3>Operatie reusita</h3><br />Inregistrarea a fost eliminata din baza de data." +
                "<br /><br /><a href='./fetch-student-list'>Inapoi la lista de studenti</a>" +
                "<br /><a href='./'>Inapoi la meniul principal</a>");
    }
}
