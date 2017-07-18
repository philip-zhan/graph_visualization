package com.talkingdata.datascience.idgraph.visualization

import org.apache.spark.graphx.{Edge, VertexId}
import org.apache.spark.rdd.RDD
import java.lang.Long

/**
  * Created by philipzhan on 2017-07-17.
  */
object ImeiVerticesAndEdgesConstructor {
  def getImeiVerticesAndEdges(deviceRDDwithUniqueID: RDD[(Device, Long)]) = {
    val imeiRDDwithUniqueID =
      deviceRDDwithUniqueID.flatMap{
        case (device, deviceUniqueId) =>
          device.imei.map{
            imei =>
              (Imei(imei), deviceUniqueId)
          }
      }.groupByKey()
        .persist()
        .zipWithUniqueId()
        .persist()

    val imeiVerticesRDD: RDD[(VertexId, VertexProperty)] =
      imeiRDDwithUniqueID.map{
        case((imeiVertexProperty, deviceUniqueIds), imeiVertexUniqueId) =>
          (Long.parseLong(("1" + imeiVertexUniqueId.toHexString.take(15)),16), imeiVertexProperty)
      }

    val imeiEdgeRDD: RDD[Edge[Boolean]] =
      imeiRDDwithUniqueID
        .flatMap{
          case ((imeiVertexProperty, deviceUniqueIds), imeiVertexUniqueId) =>
            deviceUniqueIds.map{
              deviceUniqueId =>
                Edge(deviceUniqueId, imeiVertexUniqueId, true)
            }
        }
    (imeiVerticesRDD, imeiEdgeRDD)
  }
}
