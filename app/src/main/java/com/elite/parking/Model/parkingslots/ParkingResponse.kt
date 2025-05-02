package com.elite.parking.Model.parkingslots


data class ParkingResponse(
    val mssg: String,
    val content: List<ParkingSlot>
)

data class ParkingSlot(
    val uuid: String,
    val blockNo: String,
    val floorNo: String,
    val parkingNo: String,
    val isAvailable: Boolean,
    val availabilityStatus: Int
)




// For sectioned list with headers

sealed class ListItem {
    data class BlockHeader(val block: String) : ListItem()
    data class FloorHeader(val floor: String) : ListItem()
    data class ParkingSlotItem(val slot: ParkingSlot) : ListItem()
}
data class Block(
    val blockNo: String,
    val floors: List<Floor>
)

data class Floor(
    val floorNo: String,
    val slots: List<ParkingSlot>
)

