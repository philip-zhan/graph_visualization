package com.talkingdata.datascience.idgraph.visualization

/**
  * Created by philipzhan on 2017-07-05.
  */

import java.lang.Long

import org.apache.log4j.{Level, Logger}
import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql._
import org.apache.spark.{SparkConf, SparkContext}
import org.viz.lightning.Lightning

object GraphConstructor extends App{

  def hexString2Long(hex: String): Long = {
    Long.parseLong(hex, 16)
  }

  def constructGraph(): Graph[VertexProperty, Boolean] = {


    Logger.getLogger("org").setLevel(Level.ERROR)
    Logger.getLogger("akka").setLevel(Level.ERROR)

    val sparkConf = new SparkConf().setMaster("local").setAppName("testSparkLocal")
    val sc = new SparkContext(sparkConf)
    val sqlContext = new SQLContext(sc)
    //val hdfsConf = sc.hadoopConfiguration


    val rawRDD = sc.textFile("/Users/philipzhan/talkingdata/graphx/data/device_info_10k.txt")

    val deviceRDD = rawRDD.map(_.stripPrefix("[").stripSuffix("]").split(','))
      .map {
        split =>
          new Device(
            deviceId = split(0),
            imei = split(2).stripPrefix("WrappedArray(").stripSuffix(")").split(','),
            idfa = split(3).stripPrefix("WrappedArray(").stripSuffix(")").split(','),
            mac = split(4).stripPrefix("WrappedArray(").stripSuffix(")").split(','),
            androidId = split(5).stripPrefix("WrappedArray(").stripSuffix(")").split(','),
            imsi = split(6).stripPrefix("WrappedArray(").stripSuffix(")").split(','),
            simId = split(7).stripPrefix("WrappedArray(").stripSuffix(")").split(',')
          )
      }.persist()


    val deviceRDDwithUniqueID = deviceRDD.zipWithUniqueId().map {
      case (device, id) =>
        (device, hexString2Long("0" + id.toHexString.take(15)))
    }.persist()

    val deviceIdVerticesRDD: RDD[(VertexId, VertexProperty)] =
      deviceRDDwithUniqueID.map {
        case (device, id) =>
          (id, DeviceId(device.deviceId))
      }


    def getFieldVerticesAndEdges(fieldName: String, className: String, vertexIdPrefix: String) = {
      val fieldRDDwithUniqueID =
        deviceRDDwithUniqueID.flatMap {
          case (device, deviceUniqueId) =>

            //val stringArrayInstance = Array[String]()
            val field = device.getClass.getDeclaredField(fieldName)
            //val value = field.get(stringArrayInstance)
            field.setAccessible(true)

            field.get(device)
              .asInstanceOf[Array[String]]
              .map {
                value =>
                  val instance = Class.forName(className).getDeclaredConstructor(classOf[String])
                  instance.setAccessible(true)
                  (instance.newInstance(value)
                    .asInstanceOf[VertexProperty],
                    deviceUniqueId)
              }
        }.groupByKey()
          .persist()
          .zipWithUniqueId()
          .persist()

      val fieldVerticesRDD: RDD[(VertexId, VertexProperty)] =
        fieldRDDwithUniqueID.map {
          case ((fieldVertexProperty, deviceUniqueIds), fieldVertexUniqueId) =>
            (hexString2Long(vertexIdPrefix + fieldVertexUniqueId.toHexString.take(15)), fieldVertexProperty)
        }

      val fieldEdgeRDD: RDD[Edge[Boolean]] =
        fieldRDDwithUniqueID
          .flatMap {
            case ((fieldVertexProperty, deviceUniqueIds), fieldVertexUniqueId) =>
              deviceUniqueIds.map {
                deviceUniqueId =>
                  Edge(deviceUniqueId, fieldVertexUniqueId, true)
              }
          }
      (fieldVerticesRDD, fieldEdgeRDD)
    }

    val imeiVerticesAndEdges = getFieldVerticesAndEdges(fieldName = "imei", className = "com.talkingdata.datascience.idgraph.visualization.Imei", vertexIdPrefix = "1")
    val idfaVerticesAndEdges = getFieldVerticesAndEdges(fieldName = "idfa", className = "com.talkingdata.datascience.idgraph.visualization.Idfa", vertexIdPrefix = "2")
    val macVerticesAndEdges = getFieldVerticesAndEdges(fieldName = "mac", className = "com.talkingdata.datascience.idgraph.visualization.Mac", vertexIdPrefix = "3")
    val androidIdVerticesAndEdges = getFieldVerticesAndEdges(fieldName = "androidId", className = "com.talkingdata.datascience.idgraph.visualization.AndroidId", vertexIdPrefix = "4")
    val imsiVerticesAndEdges = getFieldVerticesAndEdges(fieldName = "imsi", className = "com.talkingdata.datascience.idgraph.visualization.Imsi", vertexIdPrefix = "5")
    val simIdVerticesAndEdges = getFieldVerticesAndEdges(fieldName = "simId", className = "com.talkingdata.datascience.idgraph.visualization.SimId", vertexIdPrefix = "6")


    val verticesRDD = deviceIdVerticesRDD ++
      imeiVerticesAndEdges._1 ++
      idfaVerticesAndEdges._1 ++
      macVerticesAndEdges._1 ++
      androidIdVerticesAndEdges._1 ++
      imsiVerticesAndEdges._1 ++
      simIdVerticesAndEdges._1

    val edgesRDD = imeiVerticesAndEdges._2 ++
      idfaVerticesAndEdges._2 ++
      macVerticesAndEdges._2 ++
      androidIdVerticesAndEdges._2 ++
      imsiVerticesAndEdges._2 ++
      simIdVerticesAndEdges._2

    val graph = Graph(verticesRDD, edgesRDD)

    println("Graph construction finished")

    graph

  }
}
