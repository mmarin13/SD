import ejb.CourseEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class FetchCourseListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // prepare EntityManager
        EntityManagerFactory factory =
                Persistence.createEntityManagerFactory("bazaDeDateSQLite");
        EntityManager em = factory.createEntityManager();

        StringBuilder responseText = new StringBuilder();
        responseText.append("<h2>Lista cursuri</h2>");
        responseText.append("<table border='1'><thead><tr><th>ID</th><th>Nume curs</th><th>Nume profesor titular</th>" +
                            "<th>Numar credite</th></thead>");
        responseText.append("<tbody>");

        // get courses from database
        TypedQuery<CourseEntity> query = em.createQuery("SELECT course FROM CourseEntity course", CourseEntity.class);
        List<CourseEntity> results = query.getResultList();
        for (CourseEntity course : results) {
            responseText.append("<tr><td>" + course.getId() + "</td><td>" + course.getNume() + "</td><td>" +
                    course.getNumeTitular() + "</td><td>" + course.getNrCredite() + "</td><td>" +
                    "<a href='./delete-course?id=" + course.getId() + "'>Sterge</a></td><td>" +
                    "<a href='./formular-update-course.jsp?id=" + course.getId() + "&nume=" + course.getNume() +
                    "&titular=" + course.getNumeTitular() + "&credite=" + course.getNrCredite() +
                    "'>Actualizeaza</a></td></tr>");
        }
        responseText.append("</tbody></table><br /><br /><a href='./'>Inapoi la meniul principal</a>");

        // close EntityManager
        em.close();
        factory.close();

        // send response to client
        response.setContentType("text/html");
        response.getWriter().print(responseText.toString());
    }
}
