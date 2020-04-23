import ejb.CourseEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProcessCourseServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // read the parameters from POST request
        String nume = request.getParameter("nume");
        String numeTitular = request.getParameter("titular");
        int nrCredite = Integer.parseUnsignedInt(request.getParameter("credite"));

        // prepare EntityManager
        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        EntityManager em = factory.createEntityManager();

        // create a JPA entity and populate it
        CourseEntity course = new CourseEntity();
        course.setNume(nume);
        course.setNumeTitular(numeTitular);
        course.setNrCredite(nrCredite);

        // start a transaction and add course into database
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(course);
        transaction.commit();

        // close EntityManager
        em.close();
        factory.close();

        // send response to client
        response.setContentType("text/html");
        response.getWriter().println("Datele au fost adaugate in baza de date." +
                "<br /><br /><a href='./'>Inapoi la meniul principal</a>");
    }
}
