package com.talkingdata.datascience.idgraph.visualization

import org.apache.spark.graphx.{Edge, VertexId}
import org.apache.spark.rdd.RDD
import java.lang.Long

/**
  * Created by philipzhan on 2017-07-17.
  */
object SimIdVerticesAndEdgesConstructor {
  def getSimIdVerticesAndEdges(deviceRDDwithUniqueID: RDD[(Device, Long)]) = {
    val simIdRDDwithUniqueID =
      deviceRDDwithUniqueID.flatMap{
        case (device, deviceUniqueId) =>
          device.simId.map{
            simId =>
              (SimId(simId), deviceUniqueId)
          }
      }.groupByKey()
        .persist()
        .zipWithUniqueId()
        .persist()

    val simIdVerticesRDD: RDD[(VertexId, VertexProperty)] =
      simIdRDDwithUniqueID.map{
        case((simIdVertexProperty, deviceUniqueIds), simIdVertexUniqueId) =>
          (Long.parseLong(("1" + simIdVertexUniqueId.toHexString.take(15)),16), simIdVertexProperty)
      }

    val simIdEdgeRDD: RDD[Edge[Boolean]] =
      simIdRDDwithUniqueID
        .flatMap{
          case ((simIdVertexProperty, deviceUniqueIds), simIdVertexUniqueId) =>
            deviceUniqueIds.map{
              deviceUniqueId =>
                Edge(deviceUniqueId, simIdVertexUniqueId, true)
            }
        }
    (simIdVerticesRDD, simIdEdgeRDD)
  }
}
