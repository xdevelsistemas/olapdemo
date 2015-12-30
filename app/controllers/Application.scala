package controllers

import java.io.PrintWriter

import org.olap4j.CellSet
import org.olap4j.layout.RectangularCellSetFormatter
import play.api._
import play.api.mvc._
object Application extends Controller {

  def index = Action {
    val l : Array[String] = Array.empty
    val xconn = lib.io.gerenc.xDevOlapConnection.conn


    val query: String = "SELECT { Except( {[Product].[All Products].[Drink].[Beverages].Children}, {[Product].[All Products].[Drink].[Beverages].[Carbonated Beverages]} ) } ON COLUMNS, { [Store].[All Stores].[USA].Children } ON ROWS FROM [Sales] WHERE ([Time].[1997])"


    // Prepare a statement.
    val cellSet: CellSet = xconn.createStatement.executeOlapQuery(query)

    // We use the utility formatter.
    val formatter: RectangularCellSetFormatter = new RectangularCellSetFormatter(false)

    // Print out.
    val writer: PrintWriter = new PrintWriter(System.out)
    formatter.format(cellSet, writer)
    writer.flush()




    Ok(views.html.index("Your new application is ready."))
  }

}