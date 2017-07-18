package com.talkingdata.datascience.idgraph.visualization

import org.apache.spark.graphx.{Edge, VertexId}
import org.apache.spark.rdd.RDD
import java.lang.Long

/**
  * Created by philipzhan on 2017-07-17.
  */
object IdfaVerticesAndEdgesConstructor {
  def getIdfaVerticesAndEdges(deviceRDDwithUniqueID: RDD[(Device, Long)]) = {
    val idfaRDDwithUniqueID =
      deviceRDDwithUniqueID.flatMap{
        case (device, deviceUniqueId) =>
          device.idfa.map{
            idfa =>
              (Idfa(idfa), deviceUniqueId)
          }
      }.groupByKey()
        .persist()
        .zipWithUniqueId()
        .persist()

    val idfaVerticesRDD: RDD[(VertexId, VertexProperty)] =
      idfaRDDwithUniqueID.map{
        case((idfaVertexProperty, deviceUniqueIds), idfaVertexUniqueId) =>
          (Long.parseLong(("1" + idfaVertexUniqueId.toHexString.take(15)),16), idfaVertexProperty)
      }

    val idfaEdgeRDD: RDD[Edge[Boolean]] =
      idfaRDDwithUniqueID
        .flatMap{
          case ((idfaVertexProperty, deviceUniqueIds), idfaVertexUniqueId) =>
            deviceUniqueIds.map{
              deviceUniqueId =>
                Edge(deviceUniqueId, idfaVertexUniqueId, true)
            }
        }
    (idfaVerticesRDD, idfaEdgeRDD)
  }
}
