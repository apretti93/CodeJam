package codejam.utils.test;



import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Unit test for simple App.
 */
public abstract class TesterBase {



    final static Logger log = LoggerFactory.getLogger(TesterBase.class);

    /**
     * Create the test case
     * 
     * @param testName
     *            name of the test case
     */
    public TesterBase() {
        super();
    }

   

    private static String extractAns(String str) {
        Pattern p = Pattern.compile("Case #\\d: (.*)");
        Matcher m = p.matcher(str);
        if (m.matches()) {
            return m.group(1);
        }

        return "Error";
    }
    
    abstract protected String getOutput(String testCaseData) throws IOException;

    protected static Map<String, String> testInputData;
    private static Map<String, String> testOutputData;

    public static void initTestData(InputStream testDataStream) {
        testInputData = new HashMap<>();
        testOutputData = new HashMap<>();

        String str = null;
//        try {
//       //     str = IOUtils.toString(testDataStream);
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
        
        
        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(testDataStream);
            doc.getDocumentElement().normalize();

            NodeList nl = doc.getElementsByTagName("test");
            for (int i = 0; i < nl.getLength(); ++i) {
                Node n = nl.item(i);
                Element e = (Element) n;
                String input = e.getElementsByTagName("input").item(0)
                        .getTextContent();
                String output = e.getElementsByTagName("output").item(0)
                        .getTextContent();

                String name = n.getAttributes().getNamedItem("name")
                        .getNodeValue();

                testInputData.put(name, input);
                testOutputData.put(name, output);
            }
        } catch (Exception ex) {
            log.warn("ex",ex);
        }
    }
    
    @Test
    public void testMain() throws IOException {
        for (String name : testInputData.keySet()) {
            String input = testInputData.get(name).trim();
            String output = testOutputData.get(name).trim();
            
            if (output.equals("")) 
                continue;

            String actualOutput = extractAns(getOutput(input));

            assertTrue("\nInput " + input + "\nexpected output " + output
                    + "\n Actual " + actualOutput,
                    StringUtils.equalsIgnoreCase(output, actualOutput));
        }

    }

}
