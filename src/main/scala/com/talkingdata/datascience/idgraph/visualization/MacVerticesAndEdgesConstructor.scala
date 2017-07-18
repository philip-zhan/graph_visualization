package com.talkingdata.datascience.idgraph.visualization

import org.apache.spark.graphx.{Edge, VertexId}
import org.apache.spark.rdd.RDD
import java.lang.Long

/**
  * Created by philipzhan on 2017-07-17.
  */
object MacVerticesAndEdgesConstructor {
  def getMacVerticesAndEdges(deviceRDDwithUniqueID: RDD[(Device, Long)]) = {
    val macRDDwithUniqueID =
      deviceRDDwithUniqueID.flatMap{
        case (device, deviceUniqueId) =>
          device.mac.map{
            mac =>
              (Mac(mac), deviceUniqueId)
          }
      }.groupByKey()
        .persist()
        .zipWithUniqueId()
        .persist()

    val macVerticesRDD: RDD[(VertexId, VertexProperty)] =
      macRDDwithUniqueID.map{
        case((macVertexProperty, deviceUniqueIds), macVertexUniqueId) =>
          (Long.parseLong(("1" + macVertexUniqueId.toHexString.take(15)),16), macVertexProperty)
      }

    val macEdgeRDD: RDD[Edge[Boolean]] =
      macRDDwithUniqueID
        .flatMap{
          case ((macVertexProperty, deviceUniqueIds), macVertexUniqueId) =>
            deviceUniqueIds.map{
              deviceUniqueId =>
                Edge(deviceUniqueId, macVertexUniqueId, true)
            }
        }
    (macVerticesRDD, macEdgeRDD)
  }
}
