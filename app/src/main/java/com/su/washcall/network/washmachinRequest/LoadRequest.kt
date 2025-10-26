package com.su.washcall.network.washmachinRequest

data class LoadRequest(
    val machine_list: List<Machine>
) {
    data class Machine(
        val machine_id: Int,
        val room_name: String,
        val machine_name: String,
        val status: String
    )
}
