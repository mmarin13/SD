import beans.StudentBean;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UpdateStudentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // citire parametrii din cererea de tip POST
        int oldNrMatricol = Integer.parseUnsignedInt(request.getParameter("old_nr_matricol"));
        int nrMatricol = Integer.parseUnsignedInt(request.getParameter("nr_matricol"));
        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseInt(request.getParameter("varsta"));

        // initializare serializator Jackson
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // creare bean si populare cu date
        StudentBean studentBean = new StudentBean();
        studentBean.setNrMatricol(nrMatricol);
        studentBean.setNume(nume);
        studentBean.setPrenume(prenume);
        studentBean.setVarsta(varsta);

        // deserialize whole students collection
        File file = new File("E:\\0.SD\\JEE-Test\\student.xml");

        TypeReference<HashMap<String, StudentBean>> ref = new TypeReference<HashMap<String, StudentBean>>() { };
        HashMap<String, StudentBean> studentMap = mapper.readValue(file, ref);

        // replace updated student
        studentMap.remove("AC" + oldNrMatricol);
        studentMap.put("AC" + nrMatricol, studentBean);

        //serialize students collection into the xml file
        mapper.writeValue(file, studentMap);

        request.setAttribute("nr_matricol", nrMatricol);
        request.setAttribute("nume", nume);
        request.setAttribute("prenume", prenume);
        request.setAttribute("varsta", varsta);
        // redirectionare date catre pagina de afisare a informatiilor studentului
        request.getRequestDispatcher("./info-student.jsp").forward(request, response);
    }
}
