package lib;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;

import org.olap4j.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.layout.RectangularCellSetFormatter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
public class MondrianDriverConnectDemo {


    public static String convert(XMLStreamReader reader) throws XMLStreamException, TransformerFactoryConfigurationError, TransformerException {
        System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter stringWriter = new StringWriter();
        transformer.transform(new StAXSource(reader), new StreamResult(stringWriter));
        return stringWriter.toString();
    }


    public static String convertFromFile(String path) throws XMLStreamException , FileNotFoundException,  TransformerFactoryConfigurationError, TransformerException {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream in = new FileInputStream(path);
        XMLStreamReader streamReader = inputFactory.createXMLStreamReader(in);

        return convert(streamReader);
    }



    public static void main(String[] args) throws Exception {

        // Load the driver
        Class.forName("mondrian.olap4j.MondrianOlap4jDriver");


        // Connect
        final Connection connection = DriverManager.getConnection(
        "jdbc:mondrian:"                                                            // Driver ident

                + "Jdbc=jdbc:mysql://localhost:3306/foodmart;"                                // Relational DB
                + "JdbcUser=root;"
                + "JdbcPassword=root;"
                + "CatalogContent=\""+ convertFromFile("/Users/clayton/xdevelsistemas/java/olapdemo/conf/META-INF/foodmart/FoodMart.xml").replace("\"","\"\"") + "\";");                                        // Conteudo xml



        // We are dealing with an olap connection. we must unwrap it.
        final OlapConnection olapConnection = connection.unwrap(OlapConnection.class);

        // Check if it's all groovy
        System.out.println(
                olapConnection.getMetaData().getDriverName()
                + " -> "
                + olapConnection.getMetaData().getDatabaseProductName()
                + " version " + olapConnection.getMetaData().getDatabaseMajorVersion()
                + "." + olapConnection.getMetaData().getDatabaseMinorVersion());


        final String query = "SELECT { Except( {[Product].[All Products].[Drink].[Beverages].Children}, {[Product].[All Products].[Drink].[Beverages].[Carbonated Beverages]} ) } ON COLUMNS, { [Store].[All Stores].[USA].Children } ON ROWS FROM [Sales] WHERE ([Time].[1997])";


        // Prepare a statement.
        final CellSet cellSet = olapConnection
                .createStatement()              // Prepare a statement
                .executeOlapQuery(query);   // Execute some query

        // We use the utility formatter.
        RectangularCellSetFormatter formatter =
                new RectangularCellSetFormatter(false);

        // Print out.
        PrintWriter writer = new PrintWriter(System.out);
        formatter.format(cellSet, writer);
        writer.flush();


        // Done
        connection.close();
    }

}
