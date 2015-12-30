package lib.io.gerenc

import java.io.{FileInputStream, InputStream, FileNotFoundException, StringWriter}
import java.sql.{DriverManager, Connection}
import javax.xml.stream.{XMLStreamReader, XMLInputFactory, XMLStreamException}
import javax.xml.transform._
import javax.xml.transform.stax.StAXSource
import javax.xml.transform.stream.StreamResult

import org.olap4j.OlapConnection

/**
  * Created by clayton on 29/12/15.
  */

object xDevOlapConnection{


  //todo read from mongodb
  val conn: OlapConnection = open("conf/META-INF/foodmart/FoodMart.xml")


  /*
  * this function get xml from file and convert to xml with double quote
  * */
  private def convertFromFile(path: String): String = {
    System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl")

    val inputFactory: XMLInputFactory = XMLInputFactory.newInstance
    val in: InputStream = new FileInputStream(path)
    val streamReader: XMLStreamReader = inputFactory.createXMLStreamReader(in)
    val transformer: Transformer = TransformerFactory.newInstance.newTransformer
    val stringWriter: StringWriter = new StringWriter

    transformer.setOutputProperty(OutputKeys.INDENT, "yes")
    transformer.transform(new StAXSource(streamReader), new StreamResult(stringWriter))

    /*
    * quotes in xml converted to double quotes to parse in the connection
    * */
    stringWriter.toString.replace("\"","\"\"")
  }

  def open(path : String) : OlapConnection  = {



    if (!this.isOpen){
      // Load the driver
      Class.forName("mondrian.olap4j.MondrianOlap4jDriver")

      //TODO persist with mongo and store this values

      // read from mongodb property
      val jdbcConn = "Jdbc=jdbc:mysql://localhost:32768/foodmart"
      // read from mongodb property
      val credentials =  "JdbcUser=root;JdbcPassword=123"
      // read from mongodb property
      val catalogContent = "CatalogContent=\"" +
        convertFromFile(path) + "\";"

      // Connect
      val connection: Connection = DriverManager.getConnection("jdbc:mondrian:" + jdbcConn + ";"+ credentials + ";" + catalogContent)


      connection.unwrap(classOf[OlapConnection])

    }else{

      this.conn
    }


  }

  def isOpen =  this.conn != null && !this.conn.isClosed


  def close() = {
    if (this.conn != null && !this.conn.isClosed){
      this.conn.close()
    }
  }


}