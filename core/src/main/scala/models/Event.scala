package models

case class Event(
    val citizen: String,
    val message: String,
    val latitude: Double,
    val longitude: Double,
    val date: String,
    val battery: Int,
	  val temperature: Int,
	  val country: String
) {
  def toAlert(): String = {
	return s"--ALERT--\n\nBattery: $battery% \nCountry: $country \nLocalisation: http://maps.google.com/maps?q=$latitude,$longitude \nTemperature: $temperature°";
  }

  def this() = this(null,null,0,0,null,0,0,null)
}
