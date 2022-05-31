package edu.ib.allergyapp.pollens

data class PollenData(
    val data: List<Data>
) {
    data class Data(
        val mold_level: Int,
        val pollen_level_grass: Int,
        val pollen_level_tree: Int,
        val pollen_level_weed: Int,
        val predominant_pollen_type: String
    )
}