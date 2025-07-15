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

    fun getTotalPrice(): Double = cartItems.sumOf { it.totalPrice }
}
