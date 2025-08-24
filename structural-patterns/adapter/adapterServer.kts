/**
 * SERVER-SIDE ADAPTER EXAMPLE
 *
 * Scenario: Integrating with multiple payment gateways
 * Different payment providers have incompatible APIs, but our system
 * needs a unified interface to process payments.
 */

// Unified payment interface our system expects
interface PaymentProcessor {
    fun processPayment(amount: Double, currency: String, customerId: String): Boolean
    fun refundPayment(transactionId: String, amount: Double): Boolean
}

// Stripe payment gateway with incompatible interface
class StripeGateway {
    fun createCharge(amountCents: Int, currency: String, customer: String): StripeResponse {
        // Stripe uses cents and different parameter names
        return StripeResponse(true, "ch_123")
    }
    
    fun createRefund(chargeId: String, amountCents: Int): StripeResponse {
        return StripeResponse(true, "re_123")
    }
}

class StripeResponse(val success: Boolean, val id: String)

// PayPal payment gateway with different interface
class PayPalService {
    fun makePayment(amount: Double, curr: String, userEmail: String): PayPalResult {
        // PayPal uses different parameter names and structure
        return PayPalResult("COMPLETED", "PAY-123")
    }
    
    fun issueRefund(paymentId: String, refundAmount: Double): PayPalResult {
        return PayPalResult("REFUNDED", "REF-123")
    }
}

class PayPalResult(val status: String, val transactionId: String)

// Adapter for Stripe gateway
class StripeAdapter(private val stripe: StripeGateway) : PaymentProcessor {
    override fun processPayment(amount: Double, currency: String, customerId: String): Boolean {
        val response = stripe.createCharge((amount * 100).toInt(), currency, customerId)
        return response.success
    }
    
    override fun refundPayment(transactionId: String, amount: Double): Boolean {
        val response = stripe.createRefund(transactionId, (amount * 100).toInt())
        return response.success
    }
}

// Adapter for PayPal gateway
class PayPalAdapter(private val paypal: PayPalService) : PaymentProcessor {
    override fun processPayment(amount: Double, currency: String, customerId: String): Boolean {
        val result = paypal.makePayment(amount, currency, customerId)
        return result.status == "COMPLETED"
    }
    
    override fun refundPayment(transactionId: String, amount: Double): Boolean {
        val result = paypal.issueRefund(transactionId, amount)
        return result.status == "REFUNDED"
    }
}

// Client code - payment service doesn't need to know about different gateways
fun processServerPayment() {
    val stripeProcessor: PaymentProcessor = StripeAdapter(StripeGateway())
    val paypalProcessor: PaymentProcessor = PayPalAdapter(PayPalService())
    
    // Unified interface for all payment processors
    val success1 = stripeProcessor.processPayment(100.0, "USD", "cust_123")
    val success2 = paypalProcessor.processPayment(150.0, "EUR", "user@email.com")
    
    println("Stripe payment: $success1, PayPal payment: $success2")
}

// Run examples
fun main() {
    println("Server-side Adapter Example:")
    processServerPayment()
}

main()