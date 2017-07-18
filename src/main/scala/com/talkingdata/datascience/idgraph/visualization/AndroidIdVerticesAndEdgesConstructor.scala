package com.talkingdata.datascience.idgraph.visualization

import org.apache.spark.graphx.{Edge, VertexId}
import org.apache.spark.rdd.RDD
import java.lang.Long

/**
  * Created by philipzhan on 2017-07-17.
  */
object AndroidIdVerticesAndEdgesConstructor {
  def getAndroidIdVerticesAndEdges(deviceRDDwithUniqueID: RDD[(Device, Long)]) = {
    val androidIdRDDwithUniqueID =
      deviceRDDwithUniqueID.flatMap{
        case (device, deviceUniqueId) =>
          device.androidId.map{
            androidId =>
              (AndroidId(androidId), deviceUniqueId)
          }
      }.groupByKey()
        .persist()
        .zipWithUniqueId()
        .persist()

    val androidIdVerticesRDD: RDD[(VertexId, VertexProperty)] =
      androidIdRDDwithUniqueID.map{
        case((androidIdVertexProperty, deviceUniqueIds), androidIdVertexUniqueId) =>
          (Long.parseLong(("1" + androidIdVertexUniqueId.toHexString.take(15)),16), androidIdVertexProperty)
      }

    val androidIdEdgeRDD: RDD[Edge[Boolean]] =
      androidIdRDDwithUniqueID
        .flatMap{
          case ((androidIdVertexProperty, deviceUniqueIds), androidIdVertexUniqueId) =>
            deviceUniqueIds.map{
              deviceUniqueId =>
                Edge(deviceUniqueId, androidIdVertexUniqueId, true)
            }
        }
    (androidIdVerticesRDD, androidIdEdgeRDD)
  }
}
