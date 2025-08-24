/**
 * CLIENT-SIDE CHAIN OF RESPONSIBILITY EXAMPLE
 * 
 * Scenario: Form Validation Pipeline
 * Chain of handlers validates user input in a specific order with
 * immediate feedback and cumulative error collection.
 */

// Handler interface
interface ValidationHandler {
    fun setNext(handler: ValidationHandler): ValidationHandler
    fun validate(input: FormInput): ValidationResult
}

// Abstract base handler
abstract class AbstractValidationHandler : ValidationHandler {
    private var nextHandler: ValidationHandler? = null
    
    override fun setNext(handler: ValidationHandler): ValidationHandler {
        nextHandler = handler
        return handler
    }
    
    override fun validate(input: FormInput): ValidationResult {
        return nextHandler?.validate(input) ?: ValidationResult(true, emptyList())
    }
}

// Concrete validation handlers
class RequiredFieldHandler(private val fieldName: String) : AbstractValidationHandler() {
    override fun validate(input: FormInput): ValidationResult {
        val value = input.fields[fieldName] ?: ""
        
        if (value.isBlank()) {
            return ValidationResult(false, listOf("$fieldName is required"))
        }
        
        return super.validate(input)
    }
}

class EmailValidationHandler : AbstractValidationHandler() {
    override fun validate(input: FormInput): ValidationResult {
        val email = input.fields["email"] ?: ""
        val errors = mutableListOf<String>()
        
        if (email.isNotBlank() && !isValidEmail(email)) {
            errors.add("Invalid email format")
        }
        
        val nextResult = super.validate(input)
        return ValidationResult(
            nextResult.isValid && errors.isEmpty(),
            errors + nextResult.errors
        )
    }
    
    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".") && email.length >= 5
    }
}

class PasswordStrengthHandler : AbstractValidationHandler() {
    override fun validate(input: FormInput): ValidationResult {
        val password = input.fields["password"] ?: ""
        val errors = mutableListOf<String>()
        
        if (password.isNotBlank()) {
            if (password.length < 8) {
                errors.add("Password must be at least 8 characters")
            }
            if (!password.any { it.isDigit() }) {
                errors.add("Password must contain at least one number")
            }
            if (!password.any { it.isUpperCase() }) {
                errors.add("Password must contain at least one uppercase letter")
            }
        }
        
        val nextResult = super.validate(input)
        return ValidationResult(
            nextResult.isValid && errors.isEmpty(),
            errors + nextResult.errors
        )
    }
}

class AgeValidationHandler : AbstractValidationHandler() {
    override fun validate(input: FormInput): ValidationResult {
        val ageStr = input.fields["age"] ?: ""
        val errors = mutableListOf<String>()
        
        if (ageStr.isNotBlank()) {
            try {
                val age = ageStr.toInt()
                if (age < 13) {
                    errors.add("Must be at least 13 years old")
                } else if (age > 120) {
                    errors.add("Please enter a valid age")
                }
            } catch (e: NumberFormatException) {
                errors.add("Age must be a number")
            }
        }
        
        val nextResult = super.validate(input)
        return ValidationResult(
            nextResult.isValid && errors.isEmpty(),
            errors + nextResult.errors
        )
    }
}

class UsernameAvailabilityHandler : AbstractValidationHandler() {
    private val takenUsernames = setOf("admin", "user", "test", "guest")
    
    override fun validate(input: FormInput): ValidationResult {
        val username = input.fields["username"] ?: ""
        val errors = mutableListOf<String>()
        
        if (username.isNotBlank() && takenUsernames.contains(username.lowercase())) {
            errors.add("Username '$username' is already taken")
        }
        
        val nextResult = super.validate(input)
        return ValidationResult(
            nextResult.isValid && errors.isEmpty(),
            errors + nextResult.errors
        )
    }
}

// Data classes
data class FormInput(val fields: Map<String, String>)
data class ValidationResult(val isValid: Boolean, val errors: List<String>)

// Client-side form validator
class FormValidator {
    private val validationHandler: ValidationHandler
    
    init {
        // Build the validation chain
        val requiredFields = RequiredFieldHandler("username")
        val emailValidation = EmailValidationHandler()
        val passwordStrength = PasswordStrengthHandler()
        val ageValidation = AgeValidationHandler()
        val usernameAvailability = UsernameAvailabilityHandler()
        
        requiredFields.setNext(emailValidation)
            .setNext(passwordStrength)
            .setNext(ageValidation)
            .setNext(usernameAvailability)
        
        validationHandler = requiredFields
    }
    
    fun validateForm(input: FormInput): ValidationResult {
        println("\n🔍 Validating form data: $input")
        return validationHandler.validate(input)
    }
}

// Client-side usage
fun main() {
    println("=== Form Validation with Chain of Responsibility ===\n")
    
    val validator = FormValidator()
    
    val testCases = listOf(
        FormInput(mapOf("username" to "", "email" to "", "password" to "", "age" to "")),
        FormInput(mapOf("username" to "admin", "email" to "invalid", "password" to "weak", "age" to "12")),
        FormInput(mapOf("username" to "newuser", "email" to "test@example.com", "password" to "Strong123", "age" to "25")),
        FormInput(mapOf("username" to "john", "email" to "john@example", "password" to "Password1", "age" to "150")),
        FormInput(mapOf("username" to "test", "email" to "test@example.com", "password" to "Test123!", "age" to "30"))
    )
    
    testCases.forEachIndexed { index, formInput ->
        println("\n--- Test Case ${index + 1} ---")
        val result = validator.validateForm(formInput)
        
        if (result.isValid) {
            println("✅ Validation PASSED")
        } else {
            println("❌ Validation FAILED")
            result.errors.forEach { error -> println("   - $error") }
        }
    }
    
    println("\n=== Chain Benefits ===")
    println("• Each validation rule is separate and reusable")
    println("• Easy to add new validation rules")
    println("• Validation stops early for required fields")
    println("• Cumulative error collection for better UX")
    println("• Flexible validation order")
}

main()