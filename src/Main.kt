// Imports
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

// Represents a Document with a list of Entities
class Document(var version: String? = null, var encoding: String? = null) {

    // List to hold entities
    val documentList = mutableListOf<Entity>()

    // Add an entity to the document
    fun addEntity(entity: Entity) {
        documentList.add(entity)
    }

    // Remove an entity from the document (or all entities with a given name)
    fun removeEntity(entityName: String) {
        val entitiesToRemove = documentList.filter { it.name == entityName }
        documentList.removeAll(entitiesToRemove)
        if (entitiesToRemove.isEmpty()) {
            throw EntityNotFoundException("Entity '$entityName' not found.")
        }
    }

    // Add an attribute to a specified entity in the document
    fun addAttributeToEntity(motherEntityName: String, attributeName: String, attributeValue: String) {
        val entitiesWithName = documentList.filter { it.name == motherEntityName }
        if (entitiesWithName.isEmpty()) {
            throw EntityNotFoundException("Mother Entity '$motherEntityName' not found.")
        }
        for (entity in entitiesWithName) {
            entity.addAttribute(attributeName, attributeValue)
        }
    }

    // Rename entities globally in the Document
    fun renameEntity(oldName: String, newName: String) {
        val visitor = EntityRenamingVisitor(oldName, newName)
        this.accept(visitor)
    }

    // Rename attributes globally in the Document
    fun renameAttribute(motherEntityName: String, oldAttributeName: String, newAttributeName: String) {
        val visitor = AttributeRenamingVisitor(motherEntityName, oldAttributeName, newAttributeName)
        this.accept(visitor)
    }

    // Remove an entity globally in the Document
    fun removeEntityGlobally(entityName: String) {
        val visitor = EntityRemovalVisitor(entityName)
        this.accept(visitor)
    }

    // Remove an attribute globally in the Document
    fun removeAttributeGlobally(motherEntityName: String, attributeName: String) {
        val visitor = AttributeRemovalVisitor(motherEntityName, attributeName)
        this.accept(visitor)
    }

    // Query the document with an XML expression
    fun queryXPath(expression: String): List<Entity> {
        // Split the provided expression in parts
        val parts = expression.split("/").filter { it.isNotEmpty() }
        if (parts.isEmpty()) return emptyList()

        val visitor = XPathQueryVisitor(parts)
        this.accept(visitor)
        return visitor.matchingEntities
    }


    // Accepts a visitor
    private fun accept(visitor: XmlVisitor) {
        visitor.visit(this)
    }


    // Converts the document to an XML string
    fun toXmlString(filePath: String? = null): String {
        val visitor = XmlGenerationVisitor()
        this.accept(visitor)
        val xmlString = visitor.getXmlString()

        // Write to file if filePath is provided
        filePath?.let {
            File(it).writeText(xmlString)
        }

        return xmlString
    }

}


// Represents an Entity with attributes and daughter entities
class Entity(var name: String, var text: String? = null) {

    // Start entity only when name is validated
    init {
        validateEntityName(name)
    }

    // Mother entity of this entity
    var motherEntity: Entity? = null

    // List of daughter entities
    val daughterEntities = mutableListOf<Entity>()

    // List of attributes
    val attributeList = mutableListOf<Attribute>()

    // Add an attribute to the entity
    fun addAttribute(attributeName: String, attributeValue: String) {
        attributeList.add(Attribute(attributeName, attributeValue, this))
    }

    // Remove an attribute from the entity
    fun removeAttribute(attributeName: String) {
        val attributesToRemove = attributeList.filter { it.name == attributeName }
        attributeList.removeAll(attributesToRemove)
        if (attributesToRemove.isEmpty()) {
            throw EntityNotFoundException("Attribute '$attributeName' not found.")
        }
    }

    // Edit an attribute in the entity
    fun editAttribute(oldAttributeName: String, newAttributeName: String, newAttributeValue: String) {
        val attributesToEdit = attributeList.filter { it.name == oldAttributeName }
        if (attributesToEdit.isEmpty()) {
            throw EntityNotFoundException("Attribute '$oldAttributeName' not found.")
        }
        for (attributeToEdit in attributesToEdit) {
            val index = attributeList.indexOf(attributeToEdit)
            attributeList[index] = Attribute(newAttributeName, newAttributeValue, this)
        }
    }

    // Add a daughter to the entity
    fun addDaughterEntity(daughter: Entity) {
        daughterEntities.add(daughter)
        daughter.motherEntity = this // Set this entity as the mother of the daughter
    }


    // Accepts a visitor
    fun accept(visitor: XmlVisitor) {
        visitor.visit(this)
    }

    // Access the mother entity
    fun accessMotherEntity(): Entity? {
        if (motherEntity != null) {
            return motherEntity
        } else {
            throw EntityNotFoundException("Mother entity not found.")
        }
    }

    // Access list of daughter entities
    fun accessDaughterEntities(): MutableList<Entity> {
        return daughterEntities
    }


    // Converts the entity to an XML string
    fun toXmlString(depth: Int = 0): String {
        val visitor = XmlGenerationVisitor()
        this.accept(visitor)
        return visitor.getXmlString()
    }


    // Validate entity name according to the given rules
    private fun validateEntityName(name: String) {
        val pattern = Regex("^[a-zA-Z][a-zA-Z0-9_-]{0,63}$")
        if (!pattern.matches(name)) {
            throw IllegalArgumentException("Invalid entity name: '$name'. Entity names must consist of alphanumeric characters, underscores, or hyphens, be between 1 and 64 characters in length, start with a letter.")
        }
    }

}



// Represents an attribute of an Entity
class Attribute(var name: String, val value: String, val motherEntity: Entity?) {

    // Accepts a visitor
    fun accept(visitor: XmlVisitor) {
        visitor.visit(this)
    }
}



// Interface for visitor object
interface XmlVisitor {
    fun visit(document: Document) {}
    fun visit(entity: Entity) {}
    fun visit(attribute: Attribute) {}
}



// Visitor for Xml Generation
class XmlGenerationVisitor : XmlVisitor {

    // Start string object
    private val stringBuilder = StringBuilder()

    fun getXmlString(): String {
        return stringBuilder.toString()
    }

    override fun visit(document: Document) {
        // Add start of document
        stringBuilder.append("<?xml version=\"${document.version}\" encoding=\"${document.encoding}\"?>\n")
        document.documentList.forEach { it.accept(this) }
    }

    override fun visit(entity: Entity) {
        // Calculate indentation based on depth
        val indent = "\t".repeat(entity.depth)
        // Add entity's name
        stringBuilder.append("$indent<${entity.name}")

        // Add attributes
        if (entity.attributeList.isNotEmpty()) {
            entity.attributeList.forEach { attribute ->
                stringBuilder.append(" ${attribute.name}=\"${attribute.value}\"")
            }
        }


        // If no text / daughterEntities then end
        if (entity.text.isNullOrBlank() && entity.daughterEntities.isEmpty()) {
            stringBuilder.append("/>\n")

        } else {
            // Close opening tag
            stringBuilder.append(">")
            // Add text
            entity.text?.let { stringBuilder.append(it) }
            // Add daughter entities
            if (entity.daughterEntities.isNotEmpty()) {
                stringBuilder.append("\n")
                entity.daughterEntities.forEach { it.accept(this) }
                stringBuilder.append(indent)
            }
            // Add closing tag
            stringBuilder.append("</${entity.name}>")
            // Add a new line if the entity is not the root
            if (entity.depth > 0) {
                stringBuilder.append("\n")
            }
        }
    }


}



// Visitor for renaming entities
class EntityRenamingVisitor(private val oldName: String, private val newName: String) : XmlVisitor {

    override fun visit(document: Document) {
        for (entity in document.documentList) {
            entity.accept(this)
        }
    }

    override fun visit(entity: Entity) {
        if (entity.name == oldName) {
            entity.name = newName
        }
        for (daughter in entity.daughterEntities) {
            daughter.accept(this)
        }
    }

}



// Visitor for renaming attributes
class AttributeRenamingVisitor(
    private val motherEntityName: String,
    private val oldAttributeName: String,
    private val newAttributeName: String
) : XmlVisitor {

    override fun visit(document: Document) {
        for (entity in document.documentList) {
            entity.accept(this)
        }
    }

    override fun visit(entity: Entity) {
        if (entity.name == motherEntityName) {
            for (attribute in entity.attributeList) {
                if (attribute.name == oldAttributeName) {
                    attribute.name = newAttributeName
                }
            }
        }
        for (daughter in entity.daughterEntities) {
            daughter.accept(this)
        }
    }

}


// Visitor for removing entities
class EntityRemovalVisitor(private val entityNameToRemove: String) : XmlVisitor {

    override fun visit(document: Document) {
        val entityToRemove = document.documentList.firstOrNull { it.name == entityNameToRemove }
        if (entityToRemove != null) {
            document.documentList.remove(entityToRemove)
        }
        for (entity in document.documentList) {
            entity.accept(this)
        }
    }


}



// Visitor for removing attributes
class AttributeRemovalVisitor(private val motherEntityName: String, private val attributeName: String) : XmlVisitor {

    override fun visit(document: Document) {
        for (entity in document.documentList) {
            entity.accept(this)
        }
    }

    override fun visit(entity: Entity) {
        if (entity.name == motherEntityName) {
            val attributeToRemove = entity.attributeList.firstOrNull { it.name == attributeName }
            if (attributeToRemove != null) {
                entity.attributeList.remove(attributeToRemove)
            }
        }
        for (daughter in entity.daughterEntities) {
            daughter.accept(this)
        }
    }

}

// Visitor for querying XPath
class XPathQueryVisitor(private val parts: List<String>) : XmlVisitor {

    // List to store entities that match the XPath query
    val matchingEntities = mutableListOf<Entity>()

    override fun visit(document: Document) {
        for (entity in document.documentList) {
            searchPath(entity, 0)
        }
    }

    // Recursive function to search for matching entities along the path
    private fun searchPath(entity: Entity, partIndex: Int) {

        // If the partIndex exceeds the parts size, end
        if (partIndex >= parts.size) return

        // Check if the current entity's name matches the current part in the parts list
        if (entity.name == parts[partIndex]) {
            if (partIndex == parts.size - 1) {
                // Found the last entity in the expression, collect all descendants
                collectDescendants(entity)
            } else {
                // Continue to search for the next part in descendants
                for (daughter in entity.daughterEntities) {
                    searchPath(daughter, partIndex + 1)
                }
            }
        } else {
            // Continue to search for the first part in all descendants
            for (daughter in entity.daughterEntities) {
                searchPath(daughter, partIndex)
            }
        }
    }

    // Function to collect all descendant entities recursively
    private fun collectDescendants(entity: Entity) {
        matchingEntities.add(entity)
        for (daughter in entity.daughterEntities) {
            collectDescendants(daughter)
        }
    }
}


// Define annotations
@Target(AnnotationTarget.CLASS)
annotation class XmlEntityName(val name: String)

@Target(AnnotationTarget.PROPERTY)
annotation class XmlAttribute

@Target(AnnotationTarget.PROPERTY)
annotation class XmlEntity

@Target(AnnotationTarget.PROPERTY)
annotation class XmlIgnore

@Target(AnnotationTarget.PROPERTY)
annotation class XmlString(val transformer: KClass<out StringTransformer>)

@Target(AnnotationTarget.CLASS)
annotation class XmlAdapter(val adapter: KClass<out PostProcessor>)

interface StringTransformer {
    fun transform(value: Any): String
}

interface PostProcessor {
    fun process(entity: Entity)
}

// Function to map objects to XML Entities
fun mapObjectToXmlEntity(obj: Any): Entity {

    // Get the KClass of the object
    val kClass = obj::class

    // Determine the name of the XML entity using the @XmlEntityName annotation or the class name in lowercase
    val entityName = kClass.findAnnotation<XmlEntityName>()?.name ?: kClass.simpleName?.lowercase()
    ?: throw IllegalArgumentException("Class must have a name")

    // Create a new Entity with the determined name
    val entity = Entity(entityName)

    // Iterate over each property of the class
    kClass.memberProperties.forEach { prop ->

        // Skip the property if it has the @XmlIgnore annotation
        if (prop.findAnnotation<XmlIgnore>() != null) return@forEach

        // Determine the XML name of the property using the @XmlEntityName annotation or the property name
        val xmlName = prop.findAnnotation<XmlEntityName>()?.name ?: prop.name

        // Get the value of the property
        val value = prop.getter.call(obj)

        // If the property has the @XmlString annotation, apply the transformer
        val stringTransformer = prop.findAnnotation<XmlString>()?.transformer?.primaryConstructor?.call()

        val stringValue = if (stringTransformer != null && value != null) {
            stringTransformer.transform(value)
        } else {
            value.toString()
        }

        // If the property has the @XmlAttribute annotation, add it as an attribute to the entity
        if (prop.findAnnotation<XmlAttribute>() != null) {
            entity.addAttribute(xmlName, stringValue)
        }
        // If the property has the @XmlEntity annotation or is a List, add it as a sub-entity
        else if (prop.findAnnotation<XmlEntity>() != null || prop.returnType.classifier == List::class) {

            // Handle properties that are lists
            if (prop.returnType.classifier == List::class) {
                val listEntity = Entity(xmlName)
                val list = value as List<*>
                list.forEach { item ->
                    if (item != null) listEntity.addDaughterEntity(mapObjectToXmlEntity(item))
                }
                entity.addDaughterEntity(listEntity)

            // Handle properties that are single sub-entities
            } else {
                val subEntity = Entity(xmlName, value.toString())
                entity.addDaughterEntity(subEntity)
            }
        }
        // If the property is not annotated, add it as a sub-entity with its value
        else {
            val subEntity = Entity(xmlName, value.toString())
            entity.addDaughterEntity(subEntity)
        }
    }

    // Apply post-processing if @XmlAdapter is present
    val postProcessor = kClass.findAnnotation<XmlAdapter>()?.adapter?.primaryConstructor?.call()
    postProcessor?.process(entity)

    // Return the constructed entity
    return entity
}

// Extension property to calculate the depth of an Entity
val Entity.depth: Int

    get() {
        val mother = motherEntity
        return if(mother == null) {
            0
        } else {
            1 + mother.depth
        }
    }



// Extension property to calculate the depth of an Attribute
val Attribute.depth: Int
    get () {
        val mother = motherEntity
        return if (mother == null) {
            0
        } else {
            1 + mother.depth
        }
    }



// Exception for when an Entity is not found
class EntityNotFoundException(message: String) : Exception(message)
