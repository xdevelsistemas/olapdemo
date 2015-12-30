package controllers

import java.io.PrintWriter
import java.util.Date

import com.fasterxml.jackson.databind.{SerializationConfig, ObjectMapper}
import org.olap4j.CellSet
import org.olap4j.layout.RectangularCellSetFormatter
import org.saiku.olap.util.OlapResultSetUtil
import org.saiku.olap.util.formatter.FlattenedCellSetFormatter
import org.saiku.web.rest.objects.resultset.QueryResult
import org.saiku.web.rest.util.RestUtil
import play.api._
import play.api.mvc._
import spray.json._
import DefaultJsonProtocol._



object Application extends Controller {

  def index = Action {
    println("opa")
    val l : Array[String] = Array.empty
    val xconn = lib.io.gerenc.xDevOlapConnection.conn


    val query: String = "SELECT { Except( {[Product].[All Products].[Drink].[Beverages].Children}, {[Product].[All Products].[Drink].[Beverages].[Carbonated Beverages]} ) } ON COLUMNS, { [Store].[All Stores].[USA].Children } ON ROWS FROM [Sales] WHERE ([Time].[1997])"


    // Prepare a statement.
    val cellSet: CellSet = xconn.createStatement.executeOlapQuery(query)


    /*de trecho baixo foi inspirado de:
      * org.saiku.service.olap.OlapQueryService.{executeMdx, execute}
      * e
      * org.saiku.web.rest.resources.QueryResource.executeMdx
      *
      * Que é como eles lidam com a o CellSet, trazendo para dentro do saiku, utilizando as estruturas deles
      */
    val formatter = new FlattenedCellSetFormatter()


    val result = OlapResultSetUtil.cellSet2Matrix(cellSet)
    val queryResult = RestUtil.convert(result)





    // conversão simples com o jackson
    val mapper = new ObjectMapper()
    val json = mapper.writeValueAsString(queryResult)

    Ok(json)
  }

}