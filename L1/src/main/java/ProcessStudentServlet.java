import beans.StudentBean;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.Year;

public class ProcessStudentServlet extends HttpServlet {
    private void deleteLastTagFromXML(String xmlFilePath) throws IOException {
        RandomAccessFile f = new RandomAccessFile(xmlFilePath, "rw");
        long length = f.length() - 1;
        byte b;
        do {
            length -= 1;
            f.seek(length);
            b = f.readByte();
        } while(b != '<');
        f.setLength(length);
        f.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // citire parametrii din cererea de tip POST
        int nrMatricol = Integer.parseUnsignedInt(request.getParameter("nr_matricol"));
        String nume = request.getParameter("nume");
        String prenume = request.getParameter("prenume");
        int varsta = Integer.parseInt(request.getParameter("varsta"));

        // procesarea datelor - calcul an nastere
        int anCurent = Year.now().getValue();
        int anNastere = anCurent - varsta;

        // initializare serializator Jackson
        XmlMapper mapper = new XmlMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // creare bean si populare cu date
        StudentBean studentBean = new StudentBean();
        studentBean.setNrMatricol(nrMatricol);
        studentBean.setNume(nume);
        studentBean.setPrenume(prenume);
        studentBean.setVarsta(varsta);

        // be careful to absolute path
        File file = new File("E:\\0.SD\\JEE-Test\\student.xml");

        // check if file exists onf disc
        if (!file.exists()) {
            // at the start of the file put start tag of the root (students collection)
            // students collection is a HashMap that use <nrMatricol> to form the key
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("<HashMap>\n");
            fileWriter.close();
        } else {
            // delete the last tag from XML
            deleteLastTagFromXML("E:\\0.SD\\JEE-Test\\student.xml");
        }

        // serializare bean sub forma de string XML
        String serializedStudentBean = mapper.writeValueAsString(studentBean);

        // add new student serialized
        FileWriter fileWriter = new FileWriter(file, true);

        fileWriter.append(serializedStudentBean.replace("StudentBean",
                "AC" + Integer.toString(studentBean.getNrMatricol())));
        fileWriter.append("</HashMap>");

        fileWriter.flush();
        fileWriter.close();

        // se trimit datele primite si anul nasterii catre o alta pagina JSP pentru afisare
        request.setAttribute("nr_matricol", nrMatricol);
        request.setAttribute("nume", nume);
        request.setAttribute("prenume", prenume);
        request.setAttribute("varsta", varsta);
        request.setAttribute("anNastere", anNastere);
        request.getRequestDispatcher("./info-student.jsp").forward(request, response);
    }
}
