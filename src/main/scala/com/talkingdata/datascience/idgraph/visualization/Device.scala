package com.talkingdata.datascience.idgraph.visualization

/**
  * Created by philipzhan on 2017-07-17.
  */

case class Device (
               var deviceId:  String,
               var imei:      Array[String],
               var idfa:      Array[String],
               var mac:       Array[String],
               var androidId: Array[String],
               var imsi:      Array[String],
               var simId:     Array[String]
             )
{

}