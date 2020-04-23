import ejb.CourseEntity;

import javax.persistence.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UpdateCourseServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // read the parameters
        int id = Integer.parseUnsignedInt(request.getParameter("id"));
        String nume = request.getParameter("nume");
        String numeTitular = request.getParameter("titular");
        int nrCredite = Integer.parseUnsignedInt(request.getParameter("credite"));

        // prepare EntityManager
        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        EntityManager em = factory.createEntityManager();

        // start transaction
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        // update information about course into database
        TypedQuery<CourseEntity> query = em.createQuery("UPDATE CourseEntity course " +
                        "SET course.nume = :nume, course.numeTitular = :titular, course.nrCredite = :credite " +
                        "WHERE course.id = :id",
                        CourseEntity.class);
        query.setParameter("id", id);
        query.setParameter("nume", nume);
        query.setParameter("titular", numeTitular);
        query.setParameter("credite", nrCredite);
        query.executeUpdate();

        // commit the transaction
        transaction.commit();

        // close EntityManager
        em.close();
        factory.close();

        // send the response to client
        response.setContentType("text/html");
        response.getWriter().print("<h3>Operatie reusita</h3><br />Informatiile au fost actualizate in baza de data." +
                "<br /><br /><a href='./fetch-course-list'>Inapoi la lista de cursuri</a>" +
                "<br /><a href='./'>Inapoi la meniul principal</a>");
    }
}
