package com.talkingdata.datascience.idgraph.visualization

/**
  * Created by philipzhan on 2017-07-17.
  */

class VertexProperty()

case class DeviceId (deviceId:  String) extends VertexProperty
case class Imei     (imei:      String) extends VertexProperty
case class Idfa     (idfa:      String) extends VertexProperty
case class Mac      (mac:       String) extends VertexProperty
case class AndroidId(androidId: String) extends VertexProperty
case class Imsi     (imsi:      String) extends VertexProperty
case class SimId    (simId:     String) extends VertexProperty
