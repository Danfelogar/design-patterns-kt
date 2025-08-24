/**
 * SERVER-SIDE BRIDGE EXAMPLE
 * 
 * Scenario: Payment processing system with multiple payment methods and gateways
 * The abstraction (payment methods) and implementation (payment gateways) can vary independently.
 */

// Implementation interface - Payment Gateway
interface PaymentGateway {
    fun processPayment(amount: Double, currency: String): Boolean
    fun refundPayment(transactionId: String): Boolean
    fun getTransactionStatus(transactionId: String): String
}

// Concrete implementations - Different payment gateways
class StripeGateway : PaymentGateway {
    override fun processPayment(amount: Double, currency: String): Boolean {
        println("Processing $$amount via Stripe")
        return true
    }
    
    override fun refundPayment(transactionId: String): Boolean {
        println("Refunding transaction $transactionId via Stripe")
        return true
    }
    
    override fun getTransactionStatus(transactionId: String): String {
        return "COMPLETED"
    }
}

class PayPalGateway : PaymentGateway {
    override fun processPayment(amount: Double, currency: String): Boolean {
        println("Processing $$amount via PayPal")
        return true
    }
    
    override fun refundPayment(transactionId: String): Boolean {
        println("Refunding transaction $transactionId via PayPal")
        return true
    }
    
    override fun getTransactionStatus(transactionId: String): String {
        return "PENDING"
    }
}

// Abstraction - Payment Method
abstract class PaymentMethod(protected val gateway: PaymentGateway) {
    abstract fun pay(amount: Double): Boolean
    abstract fun refund(): Boolean
    abstract fun getStatus(): String
}

// Refined abstractions - Different payment methods
class CreditCardPayment(gateway: PaymentGateway, private val transactionId: String) : PaymentMethod(gateway) {
    override fun pay(amount: Double): Boolean {
        println("Processing credit card payment")
        return gateway.processPayment(amount, "USD")
    }
    
    override fun refund(): Boolean {
        return gateway.refundPayment(transactionId)
    }
    
    override fun getStatus(): String {
        return gateway.getTransactionStatus(transactionId)
    }
}

class DigitalWalletPayment(gateway: PaymentGateway, private val transactionId: String) : PaymentMethod(gateway) {
    override fun pay(amount: Double): Boolean {
        println("Processing digital wallet payment")
        return gateway.processPayment(amount, "USD")
    }
    
    override fun refund(): Boolean {
        return gateway.refundPayment(transactionId)
    }
    
    override fun getStatus(): String {
        return gateway.getTransactionStatus(transactionId)
    }
}

// Client code examples
fun main() {
    println("=== SERVER-SIDE EXAMPLE ===")
    
    // Different combinations of payment methods and gateways
    val stripe = StripeGateway()
    val paypal = PayPalGateway()
    
    val creditCardStripe = CreditCardPayment(stripe, "txn_123")
    val creditCardPayPal = CreditCardPayment(paypal, "txn_456")
    val walletStripe = DigitalWalletPayment(stripe, "txn_789")
    val walletPayPal = DigitalWalletPayment(paypal, "txn_101")
    
    creditCardStripe.pay(100.0)
    walletPayPal.pay(50.0)
}

main()