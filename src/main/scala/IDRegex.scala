/**
  * Created by philipzhan on 2017-07-11.
  */
object IDRegex {

  // TODO: hash
  // 33 bits
  val TDID_REGEX = "^[0-9a-zA-Z]{33}$"

  // XX:XX:XX:XX:XX:XX
  val MAC_REGEX = "^[0-9a-zA-Z]{2}(:[0-9a-z]{2}){5}$"

  // TODO: hash
  // format: XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX, 8-4-4-4-12
  val IDFA_REGEX = "^[0-9a-zA-Z]{8}(-[0-9a-zA-Z]{4}){3}(-[0-9a-zA-Z]{12})$"

  // 14-16 bits
  val IMEI_REGEX = "^[0-9a-zA-Z]{14,16}$"

  // 16 bits
  val ANDROIDID_REGEX = "^[0-9a-zA-Z]{16}$"

  // TODO: hash
  // 20 bits
  val SIM_REGEX = "^[0-9]{20}$"

  // 15 bits
  val IMSI_REGEX = "^[0-9]{15}$"

  val IP_REGEX = "^[0-9]{2,3}(\\.[0-9]{2,3}){3}$"


}
