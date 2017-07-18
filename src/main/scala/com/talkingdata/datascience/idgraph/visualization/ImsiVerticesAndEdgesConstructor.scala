package com.talkingdata.datascience.idgraph.visualization

import org.apache.spark.graphx.{Edge, VertexId}
import org.apache.spark.rdd.RDD
import java.lang.Long

/**
  * Created by philipzhan on 2017-07-17.
  */
object ImsiVerticesAndEdgesConstructor {
  def getImsiVerticesAndEdges(deviceRDDwithUniqueID: RDD[(Device, Long)]) = {
    val imsiRDDwithUniqueID =
      deviceRDDwithUniqueID.flatMap{
        case (device, deviceUniqueId) =>
          device.imsi.map{
            imsi =>
              (Imsi(imsi), deviceUniqueId)
          }
      }.groupByKey()
        .persist()
        .zipWithUniqueId()
        .persist()

    val imsiVerticesRDD: RDD[(VertexId, VertexProperty)] =
      imsiRDDwithUniqueID.map{
        case((imsiVertexProperty, deviceUniqueIds), imsiVertexUniqueId) =>
          (Long.parseLong(("1" + imsiVertexUniqueId.toHexString.take(15)),16), imsiVertexProperty)
      }

    val imsiEdgeRDD: RDD[Edge[Boolean]] =
      imsiRDDwithUniqueID
        .flatMap{
          case ((imsiVertexProperty, deviceUniqueIds), imsiVertexUniqueId) =>
            deviceUniqueIds.map{
              deviceUniqueId =>
                Edge(deviceUniqueId, imsiVertexUniqueId, true)
            }
        }
    (imsiVerticesRDD, imsiEdgeRDD)
  }
}
