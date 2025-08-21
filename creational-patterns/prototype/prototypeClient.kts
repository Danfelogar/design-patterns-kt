/**
 * Prototype Pattern for Document Templates - Client Side
 * Reference: https://refactoring.guru/es/design-patterns/prototype
 */

// Base document prototype
abstract class Document {
    var title: String = ""
    var content: String = ""
    var author: String = ""
    var createdAt: String = java.time.LocalDateTime.now().toString()
    var metadata: Map<String, String> = emptyMap()

    // Normal constructor
    constructor()

    // Prototype constructor
    constructor(source: Document) {
        this.title = source.title
        this.content = source.content
        this.author = source.author
        this.createdAt = source.createdAt
        this.metadata = source.metadata.toMutableMap()
    }

    abstract fun clone(): Document

    open fun display(): String {
        return """
        |Title: $title
        |Author: $author
        |Created: $createdAt
        |Content: ${content.take(50)}...
        |Metadata: $metadata
        """.trimMargin()
    }

    override fun toString(): String {
        return "${this::class.simpleName}('$title')"
    }
}

// Concrete document prototypes
class Invoice : Document {
    var invoiceNumber: String = ""
    var clientName: String = ""
    var amount: Double = 0.0
    var dueDate: String = ""

    constructor() : super()

    constructor(source: Invoice) : super(source) {
        this.invoiceNumber = source.invoiceNumber
        this.clientName = source.clientName
        this.amount = source.amount
        this.dueDate = source.dueDate
    }

    override fun clone(): Document {
        return Invoice(this)
    }

    override fun display(): String {
        return """
        |INVOICE: $invoiceNumber
        |Client: $clientName
        |Amount: $${amount}
        |Due: $dueDate
        |Title: $title
        |Author: $author
        """.trimMargin()
    }
}

class Contract : Document {
    var parties: List<String> = emptyList()
    var effectiveDate: String = ""
    var expirationDate: String = ""
    var isSigned: Boolean = false

    constructor() : super()

    constructor(source: Contract) : super(source) {
        this.parties = source.parties.toList()
        this.effectiveDate = source.effectiveDate
        this.expirationDate = source.expirationDate
        this.isSigned = source.isSigned
    }

    override fun clone(): Document {
        return Contract(this)
    }

    override fun display(): String {
        return """
        |CONTRACT: $title
        |Parties: ${parties.joinToString()}
        |Effective: $effectiveDate
        |Expires: $expirationDate
        |Signed: $isSigned
        |Author: $author
        """.trimMargin()
    }
}

class Report : Document {
    var reportType: String = ""
    var sections: List<String> = emptyList()
    var isFinal: Boolean = false

    constructor() : super()

    constructor(source: Report) : super(source) {
        this.reportType = source.reportType
        this.sections = source.sections.toList()
        this.isFinal = source.isFinal
    }

    override fun clone(): Document {
        return Report(this)
    }
}

// Document Template Registry
class DocumentTemplateRegistry {
    private val templates = mutableMapOf<String, Document>()

    init {
        loadDefaultTemplates()
    }

    private fun loadDefaultTemplates() {
        // Invoice template
        val invoiceTemplate = Invoice().apply {
            title = "Standard Invoice Template"
            author = "Accounting Department"
            content = "Invoice details: ..."
            invoiceNumber = "TEMPLATE"
            clientName = "[Client Name]"
            amount = 0.0
            dueDate = "[Due Date]"
            metadata = mapOf("category" to "financial", "version" to "1.0")
        }
        templates["standard_invoice"] = invoiceTemplate

        // Contract template
        val contractTemplate = Contract().apply {
            title = "Service Agreement Template"
            author = "Legal Department"
            content = "This agreement is made between..."
            parties = listOf("[Party A]", "[Party B]")
            effectiveDate = "[Effective Date]"
            expirationDate = "[Expiration Date]"
            metadata = mapOf("category" to "legal", "version" to "2.1")
        }
        templates["service_contract"] = contractTemplate

        // Report template
        val reportTemplate = Report().apply {
            title = "Monthly Report Template"
            author = "Management"
            content = "Executive summary: ..."
            reportType = "Monthly"
            sections = listOf("Summary", "Analysis", "Conclusions")
            metadata = mapOf("category" to "reporting", "version" to "1.2")
        }
        templates["monthly_report"] = reportTemplate
    }

    fun registerTemplate(key: String, template: Document) {
        templates[key] = template
    }

    fun getTemplate(key: String): Document? {
        return templates[key]?.clone()
    }

    fun listTemplates(): List<String> {
        return templates.keys.toList()
    }
}

// Client Application
class DocumentManagementClient {
    private val templateRegistry = DocumentTemplateRegistry()

    fun demonstrateDocumentPrototypes() {
        println("📄 === Document Prototype Demonstration - Client Side ===")
        
        // Create documents from templates
        println("\n1. 🧾 Creating Invoice from Template:")
        val invoiceTemplate = templateRegistry.getTemplate("standard_invoice") as? Invoice
        if (invoiceTemplate != null) {
            invoiceTemplate.invoiceNumber = "INV-2024-001"
            invoiceTemplate.clientName = "Acme Corp"
            invoiceTemplate.amount = 1250.75
            invoiceTemplate.dueDate = "2024-12-31"
            invoiceTemplate.title = "Invoice for Consulting Services"
            println(invoiceTemplate.display())
        }

        println("\n2. 📝 Creating Contract from Template:")
        val contractTemplate = templateRegistry.getTemplate("service_contract") as? Contract
        if (contractTemplate != null) {
            contractTemplate.title = "Web Development Agreement"
            contractTemplate.parties = listOf("Tech Solutions Inc.", "Global Enterprises Ltd.")
            contractTemplate.effectiveDate = "2024-01-15"
            contractTemplate.expirationDate = "2025-01-14"
            contractTemplate.isSigned = true
            println(contractTemplate.display())
        }

        println("\n3. 📊 Creating Multiple Reports from Template:")
        val reportTemplate = templateRegistry.getTemplate("monthly_report") as? Report
        val reports = mutableListOf<Report>()
        
        if (reportTemplate != null) {
            listOf("Q1 Report", "Q2 Report", "Q3 Report").forEachIndexed { index, title ->
                val report = reportTemplate.clone() as Report
                report.title = title
                report.reportType = "Quarterly"
                report.isFinal = index > 0
                reports.add(report)
            }
        }

        reports.forEachIndexed { index, report ->
            println("Report ${index + 1}: ${report.title} (Final: ${report.isFinal})")
        }

        println("\n4. 🔄 Template Registry Contents:")
        println("Available templates: ${templateRegistry.listTemplates().joinToString()}")
    }

    fun demonstrateDeepCloning() {
        println("\n" + "=".repeat(50))
        println("🔄 Deep Cloning Demonstration")
        
        val original = templateRegistry.getTemplate("standard_invoice") as? Invoice
        val clone1 = original?.clone() as? Invoice
        val clone2 = original?.clone() as? Invoice

        // Modify clones independently
        clone1?.apply {
            invoiceNumber = "INV-2024-002"
            amount = 2000.0
        }

        clone2?.apply {
            invoiceNumber = "INV-2024-003"
            clientName = "Different Client"
            amount = 3000.0
        }

        println("Original: ${original?.invoiceNumber} - $${original?.amount}")
        println("Clone 1: ${clone1?.invoiceNumber} - $${clone1?.amount}")
        println("Clone 2: ${clone2?.invoiceNumber} - $${clone2?.amount}")
        println("All different objects: ${original !== clone1 && clone1 !== clone2}")
    }
}

// Main function
fun main() {
    println("🔷 Client-Side Prototype Pattern: Document Templates")
    println("=".repeat(60))
    
    val client = DocumentManagementClient()
    client.demonstrateDocumentPrototypes()
    client.demonstrateDeepCloning()
    
    println("\n" + "=".repeat(60))
    println("✅ Client demonstration completed!")
    println("=".repeat(60))
}

// Execute
main()