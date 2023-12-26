package com.example.demo_product

data class PromotionTrack(
    var isMandatory: Boolean = false,
    var productTitle: String = "",
    var offerTitle: String = "",
    var isSamePromotion: Boolean = false,
    var samePromotionData: SamePromotionData? = null,
    var closeReason: CloseReason? = null
)

data class SamePromotionData(
    var isSamePos: Boolean = false,
    var samePosData: SamePosData? = null,
    var closeReason: CloseReason? = null
)

data class SamePosData(
    var shelfTracker: Boolean = false,
    var shelfTrackerData: ShelfTrackerData? = null,
    var closeReason: CloseReason? = null
)

data class ShelfTrackerData(
    var facingCount: Int = 0,
    var stockCount: Int = 0,
    var imagePath: String? = null
)

data class CloseReason(
    var imagePath: String? = null,
    var reason: String = "",
    var promotionType: String = "",
    var enterPromotionType: String = "",
)