package expo.modules.androidsmssender

data class SimCard(
  val id: Int,
  val displayName: String,
  val carrierName: String,
  val slotIndex: Int?
)
