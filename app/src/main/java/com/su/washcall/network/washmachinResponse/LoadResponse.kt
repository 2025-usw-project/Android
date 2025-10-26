package com.su.washcall.network.washmachinResponse

data class LoadResponse(
    val message: String,
    val machine_list: List<Machine>
) {
    data class Machine(
        val machine_id: Int,
        val room_name: String,
        val machine_name: String,
        val status: String
    )
}
