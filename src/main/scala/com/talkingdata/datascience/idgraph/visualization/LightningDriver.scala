package com.talkingdata.datascience.idgraph.visualization

import org.viz.lightning.Lightning

import scala.io.Source
import scala.util.parsing.json.JSON
import scalaj.http
import scalaj.http.Http
/**
  * Created by philipzhan on 2017-07-17.
  */
object LightningDriver extends App{

  val lgn = Lightning(host = "http://localhost:3000")
  lgn.createSession("test_from_intellij")

  //lgn.scatter(Array(1.0,2.0,3.0), Array(1.0,1.5,5.0), label=Array(1,2,3))
  lgn.plot(name = "force", data =
    Map(
      ("nodes", Array[Int](0, 1, 2, 3, 4, 5, 6, 7, 8)),
      ("group", Array[Int](1, 1, 1, 1, 2, 2, 2, 3, 3)),
      ("labels", Array[String]("Myriel", "Napoleon", "Mlle", "Baptistine", "Mme", "ContessdeLo", "Geborand", "Champtercier", "Cravatte")),
      ("links", Array[Array[Int]](Array[Int](1, 0), Array[Int](2, 0), Array[Int](3, 0), Array[Int](3, 2), Array[Int](4, 0), Array[Int](5, 0), Array[Int](6, 0), Array[Int](7, 0), Array[Int](8, 0)))
  )
  )

  case class ForceVertex(id: Long, group: Int, label: String)

  val graph = GraphConstructor.constructGraph()
  //graph.vertices.take(10).foreach(vertex => println("ID: " + vertex._1.toHexString + " Property: " + vertex._2))
  graph.vertices.zipWithUniqueId().map{
    case((vertexId, vertexProperty), uniqueId) =>
      ForceVertex(uniqueId, vertexProperty)
  }

//  val url = "http://localhost:3000/sessions/"
//  val init = Http(url).postData("""name=my session""").asString.body
//  val temp = init.substring(7)
//  val sessionID = temp.substring(0, temp.indexOf('"'))
//  val post = Http(url + sessionID + "/visualizations")
//    .postData("""type=line""")
//    .postData("""data={"series": [1, 2, 3, 4, 5]}""")
//  println(post.asString.headers)
}
