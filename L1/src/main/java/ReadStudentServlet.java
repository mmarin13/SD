import beans.StudentBean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ReadStudentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        // read the key that is used to search
        String nrMatricol = "AC" + request.getParameter("nr_matricol");

        // deserializare student din fisierul XML de pe disc
        File file = new File("E:\\0.SD\\JEE-Test\\student.xml");

        // returneaza raspuns HTTP de tip 404 in caz ca fisierul nu exista
        if (!file.exists()) {
            response.sendError(404,"Nu a fost gasita nicio colectie de studenti serializata pe disc.");
            return;
        }

        // students collection deserialization
        XmlMapper mapper = new XmlMapper();
        TypeReference<HashMap<String, StudentBean>> ref = new TypeReference<HashMap<String, StudentBean>>() { };
        HashMap<String, StudentBean> studentMap = mapper.readValue(file, ref);

        if (studentMap.containsKey(nrMatricol)) {
            StudentBean studentBean = studentMap.get(nrMatricol);
            request.setAttribute("nr_matricol", studentBean.getNrMatricol());
            request.setAttribute("nume", studentBean.getNume());
            request.setAttribute("prenume", studentBean.getPrenume());
            request.setAttribute("varsta", studentBean.getVarsta());
            // redirectionare date catre pagina de afisare a informatiilor studentului
            request.getRequestDispatcher("./info-student.jsp").forward(request, response);
        } else {
            response.sendError(404, "Niciun student nu a fost inregistrat cu acest numar matricol.");
        }
    }
}
