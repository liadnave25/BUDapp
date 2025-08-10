import com.example.bud.dataclasses.OrderItem

object CartManager {
    private val cartItems = mutableListOf<OrderItem>()
    private var nurseryName: String = ""

    fun setNurseryName(name: String) {
        nurseryName = name
    }

    fun getNurseryName(): String = nurseryName

    fun addItem(item: OrderItem) {
        cartItems.add(item)
    }

    fun getItems(): List<OrderItem> = cartItems.toList()

    fun clearCart() {
        cartItems.clear()
        nurseryName = "" // אפס גם את שם המשתלה
    }

    fun getTotalPrice(): Double =
        cartItems.sumOf { it.unitPrice * it.quantity }

    fun setNurseryNameWithReset(name: String): Boolean {
        val needReset = nurseryName.isNotEmpty() && nurseryName != name
        if (needReset) {
            cartItems.clear()
        }
        nurseryName = name
        return needReset
    }

    // מחיקת פריט לפי אינדקס
    fun removeAt(index: Int) {
        if (index in cartItems.indices) {
            cartItems.removeAt(index)
        }
    }

    // עדכון כמות פריט לפי אינדקס (לא פחות מ-1)
    fun updateQuantity(index: Int, newQty: Int) {
        if (index in cartItems.indices) {
            val item = cartItems[index]
            val fixedQty = newQty.coerceAtLeast(1)
            cartItems[index] = item.copy(quantity = fixedQty)
        }
    }
}
