
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestXML {
    val document = Document()


    // 1. Add and Remove Entities

    @Test
    fun testAddEntity() {

        val entity1 = Entity("entity1")
        document.addEntity(entity1)
        assertEquals(1, document.documentList.size)
        assertEquals("entity1", document.documentList[0].name)

        val entity2 = Entity("entity2")
        document.addEntity(entity2)
        assertEquals(2, document.documentList.size)
        assertEquals("entity2", document.documentList[1].name)

        val entity3 = Entity("entity1", "entity1")
        document.addEntity(entity3)
        assertEquals(3, document.documentList.size)
        assertEquals("entity1", document.documentList[2].name)

        // Invalid entity name - should throw IllegalArgumentException
        assertThrows<IllegalArgumentException> {
            document.addEntity(Entity("invalid-entity-name-!@#"))
        }

    }

    @Test
    fun testRemoveEntity() {
        // Add some entities
        document.addEntity(Entity("entity1"))
        document.addEntity(Entity("entity2"))
        document.addEntity(Entity("entity3"))

        // Check initial size
        assertEquals(3, document.documentList.size)

        // Remove an existing entity
        document.removeEntity("entity2")
        assertEquals(2, document.documentList.size)
        assertEquals("entity1", document.documentList[0].name)
        assertEquals("entity3", document.documentList[1].name)

        // Remove a non-existing entity
        val exception = assertThrows(EntityNotFoundException::class.java) {
            document.removeEntity("nonExistingEntity")
        }
        assertEquals("Entity 'nonExistingEntity' not found.", exception.message)

        // Remove a duplicate entity
        document.addEntity(Entity("entity3"))
        document.removeEntity("entity3")
        assertEquals(1, document.documentList.size)
        assertEquals("entity1", document.documentList[0].name)

    }


    // 2. Add, remove and alter attributes

    @Test
    fun testAddAttribute() {
        val entity = Entity("testEntity")
        val entity2 = Entity("testEntity")

        // Add attributes
        entity.addAttribute("attribute1", "value1")
        assertEquals(1, entity.attributeList.size)
        assertEquals("attribute1", entity.attributeList[0].name)
        assertEquals("value1", entity.attributeList[0].value)

        entity.addAttribute("attribute2", "value2")
        assertEquals(2, entity.attributeList.size)
        assertEquals("attribute2", entity.attributeList[1].name)
        assertEquals("value2", entity.attributeList[1].value)
    }

    @Test
    fun testRemoveAttribute() {
        val entity = Entity("testEntity")
        entity.addAttribute("attribute1", "value1")
        entity.addAttribute("attribute2", "value2")

        // Check initial size
        assertEquals(2, entity.attributeList.size)

        // Remove an existing attribute
        entity.removeAttribute("attribute2")
        assertEquals(1, entity.attributeList.size)
        assertEquals("attribute1", entity.attributeList[0].name)

        // Remove a non-existing attribute
        val exception = assertThrows(EntityNotFoundException::class.java) {
            entity.removeAttribute("nonExistingAttribute")
        }
        assertEquals("Attribute 'nonExistingAttribute' not found.", exception.message)
    }

    @Test
    fun testEditAttribute() {
        val entity = Entity("testEntity")
        entity.addAttribute("attribute1", "value1")
        entity.addAttribute("attribute2", "value2")

        // Edit an existing attribute
        entity.editAttribute("attribute1", "newAttribute1", "newValue1")
        assertEquals(2, entity.attributeList.size) // Still 2 attributes
        assertEquals("newAttribute1", entity.attributeList[0].name) // Changed name
        assertEquals("newValue1", entity.attributeList[0].value) // Changed value

        // Edit a non-existing attribute should throw an exception
        val exception = assertThrows(EntityNotFoundException::class.java) {
            entity.editAttribute("nonExistingAttribute", "newAttribute", "newValue1")
        }
        assertEquals("Attribute 'nonExistingAttribute' not found.", exception.message)
    }


    // 3. Access mother and daughter entities

    @Test
    fun testMotherAndDaughterEntities() {

        val motherEntity = Entity("motherEntity")
        val daughter1 = Entity("daughter1")
        val daughter2 = Entity("daughter2")

        // Add daughters to mother
        motherEntity.addDaughterEntity(daughter1)
        motherEntity.addDaughterEntity(daughter2)

        // Check mother entity
        assertEquals(motherEntity, daughter1.motherEntity)
        assertEquals(motherEntity, daughter2.motherEntity)

    }

    @Test
    fun testAccessMotherEntitySuccess() {
        // Setup
        val motherEntity = Entity("motherEntity")
        val daughter = Entity("daughter")

        // Adding daughter to mother
        motherEntity.addDaughterEntity(daughter)

        // Act and Assert
        assertEquals(motherEntity, daughter.accessMotherEntity(), "The mother entity should match the expected mother.")
    }

    @Test
    fun testAccessMotherEntityFailure() {
        // Setup
        val orphan = Entity("orphan")

        // Act and Assert
        val exception = assertThrows(EntityNotFoundException::class.java) {
            orphan.accessMotherEntity()
        }

        assertEquals(
            "Mother entity not found.",
            exception.message,
            "The exception message should indicate that no mother entity was found."
        )
    }

    @Test
    fun testAccessDaughterEntities() {
        // Setup
        val motherEntity = Entity("motherEntity")
        val daughter1 = Entity("daughter1")
        val daughter2 = Entity("daughter2")

        // Add daughters to mother
        motherEntity.addDaughterEntity(daughter1)
        motherEntity.addDaughterEntity(daughter2)

        // Act
        val daughters = motherEntity.accessDaughterEntities()

        // Assert
        assertEquals(2, daughters.size, "There should be two daughter entities.")
        assertTrue(daughters.contains(daughter1), "The list should contain daughter1.")
        assertTrue(daughters.contains(daughter2), "The list should contain daughter2.")
    }


    // Test depth
    @Test
    fun testDepth() {

        // Create a new Document
        val document = Document(version = "1.0", encoding = "UTF-8")

        // Create entities

        // Lvl 1
        val planoEntity = Entity("plano")
        // Add root entity to the document
        document.addEntity(planoEntity)

        // Lvl 2
        val cursoEntity = Entity("curso", "Mestrado em Engenharia Informática")
        planoEntity.addDaughterEntity(cursoEntity)

        val fucEntity = Entity("fuc")
        fucEntity.addAttribute("codigo", "M4310")
        planoEntity.addDaughterEntity(fucEntity)

        // Lvl 3
        val nomeEntity = Entity("nome", "Programação Avançada")
        fucEntity.addDaughterEntity(nomeEntity)
        val ectsEntity = Entity("ects", "6.0")
        fucEntity.addDaughterEntity(ectsEntity)
        val avaliacaoEntity = Entity("avaliacao")
        fucEntity.addDaughterEntity(avaliacaoEntity)

        // Lvl 4
        val componenteEntity1 = Entity("componente")
        componenteEntity1.addAttribute("nome", "Quizzes")
        componenteEntity1.addAttribute("peso", "20%")
        avaliacaoEntity.addDaughterEntity(componenteEntity1)

        val componenteEntity2 = Entity("componente")
        componenteEntity2.addAttribute("nome", "Projeto")
        componenteEntity2.addAttribute("peso", "80%")
        avaliacaoEntity.addDaughterEntity(componenteEntity2)

        // Assert levels of depth match
        assertEquals(planoEntity.depth, 0)
        assertEquals(cursoEntity.depth, 1)
        assertEquals(fucEntity.depth, 1)
        assertEquals(nomeEntity.depth, 2)
        assertEquals(ectsEntity.depth, 2)
        assertEquals(avaliacaoEntity.depth, 2)
        assertEquals(componenteEntity1.depth, 3)
        assertEquals(componenteEntity2.depth, 3)
    }


    // 4. Pretty print string format and write to file

    @Test
    fun testToXmlString() {
        // Create a new Document
        val document = Document(version = "1.0", encoding = "UTF-8")

        // Create entities

        // Lvl 1
        val planoEntity = Entity("plano")
        // Add root entity to the document
        document.addEntity(planoEntity)

        // Lvl 2
        val cursoEntity = Entity("curso", "Mestrado em Engenharia Informática")
        planoEntity.addDaughterEntity(cursoEntity)

        val fucEntity = Entity("fuc")
        fucEntity.addAttribute("codigo", "M4310")
        planoEntity.addDaughterEntity(fucEntity)


        // Lvl 3
        val nomeEntity = Entity("nome", "Programação Avançada")
        fucEntity.addDaughterEntity(nomeEntity)
        val ectsEntity = Entity("ects", "6.0")
        fucEntity.addDaughterEntity(ectsEntity)
        val avaliacaoEntity = Entity("avaliacao")
        fucEntity.addDaughterEntity(avaliacaoEntity)

        // Lvl 4
        val componenteEntity1 = Entity("componente")
        componenteEntity1.addAttribute("nome", "Quizzes")
        componenteEntity1.addAttribute("peso", "20%")
        avaliacaoEntity.addDaughterEntity(componenteEntity1)

        val componenteEntity2 = Entity("componente")
        componenteEntity2.addAttribute("nome", "Projeto")
        componenteEntity2.addAttribute("peso", "80%")
        avaliacaoEntity.addDaughterEntity(componenteEntity2)


        // Expected XML string
        val expectedXml = """<?xml version="1.0" encoding="UTF-8"?>
<plano>
	<curso>Mestrado em Engenharia Informática</curso>
	<fuc codigo="M4310">
		<nome>Programação Avançada</nome>
		<ects>6.0</ects>
		<avaliacao>
			<componente nome="Quizzes" peso="20%"/>
			<componente nome="Projeto" peso="80%"/>
		</avaliacao>
	</fuc>
</plano>
    """.trimIndent()

        // Convert document to XML string
        val actualXml = document.toXmlString("xmlStringOutput.txt")

        println(actualXml)

        // Assertion to compare expected and actual XML strings
        assertEquals(expectedXml, actualXml)

    }


    // 5. Swipe document using Visitor objects

    // 6. Add attributes globally to the document

    @Test
    fun testAttributeClass() {

        // Create a new entity
        val entity = Entity("testEntity")

        // Add attributes to the entity
        entity.addAttribute("attribute1", "value1")
        entity.addAttribute("attribute2", "value2")

        // Assertions for attribute list
        assertEquals(2, entity.attributeList.size)
        assertEquals("attribute1", entity.attributeList[0].name)
        assertEquals("attribute2", entity.attributeList[1].name)

        // Remove an attribute
        entity.removeAttribute("attribute1")
        assertEquals(1, entity.attributeList.size)
        assertEquals("attribute2", entity.attributeList[0].name)

        // Test for removing a non-existing attribute
        val exception = assertThrows(EntityNotFoundException::class.java) {
            entity.removeAttribute("nonExistingAttribute")
        }
        assertEquals("Attribute 'nonExistingAttribute' not found.", exception.message)

        // Edit an attribute
        entity.editAttribute("attribute2", "newAttribute2", "newValue2")
        assertEquals("newAttribute2", entity.attributeList[0].name)

    }


    @Test
    fun testAddAttributeToEntity() {
        // Create a new Document
        val document = Document()

        // Add entities to the document
        document.addEntity(Entity("motherEntity"))
        document.addEntity(Entity("daughter1"))
        document.addEntity(Entity("daughter2"))

        // Add attributes to entities using the new method
        document.addAttributeToEntity("motherEntity", "attribute1", "value1")
        document.addAttributeToEntity("daughter1", "daughterAttribute1", "dvalue1")
        document.addAttributeToEntity("daughter2", "daughterAttribute2", "dvalue2")

        // Assertions for attributes
        assertEquals(1, document.documentList[0].attributeList.size)
        assertEquals("attribute1", document.documentList[0].attributeList[0].name)
        assertEquals("value1", document.documentList[0].attributeList[0].value)

        assertEquals(1, document.documentList[1].attributeList.size)
        assertEquals("daughterAttribute1", document.documentList[1].attributeList[0].name)
        assertEquals("dvalue1", document.documentList[1].attributeList[0].value)

        assertEquals(1, document.documentList[2].attributeList.size)
        assertEquals("daughterAttribute2", document.documentList[2].attributeList[0].name)
        assertEquals("dvalue2", document.documentList[2].attributeList[0].value)
    }


    // 7. Rename entities globally in the document

    @Test
    fun testRenameEntity() {
        // Add entities to the document
        document.addEntity(Entity("oldName1"))
        document.addEntity(Entity("oldName2"))
        document.addEntity(Entity("oldName3"))

        // Rename entities
        document.renameEntity("oldName1", "newName1")
        document.renameEntity("oldName2", "newName2")
        document.renameEntity("nonExistingName", "shouldNotChange")

        // Assertions for renamed entities
        assertEquals(3, document.documentList.size)
        assertEquals("newName1", document.documentList[0].name)
        assertEquals("newName2", document.documentList[1].name)
        assertEquals("oldName3", document.documentList[2].name) // Should not change
    }


    // 8. Rename attributes globally in the document

    @Test
    fun testRenameAttribute() {
        val document = Document()

        // Create entities with attributes
        document.addEntity(Entity("motherEntity"))
        document.addAttributeToEntity("motherEntity", "attribute1", "value1")
        document.addAttributeToEntity("motherEntity", "attribute2", "value2")

        // Create another entity with attributes
        document.addEntity(Entity("anotherEntity"))
        document.addAttributeToEntity("anotherEntity", "attribute1", "value1")
        document.addAttributeToEntity("anotherEntity", "attribute2", "value2")

        // Rename attribute1 globally in motherEntity
        document.renameAttribute("motherEntity", "attribute1", "newAttribute1")

        // Check if attribute1 was renamed in motherEntity and not in anotherEntity
        val motherEntity = document.documentList.firstOrNull { it.name == "motherEntity" }
        val anotherEntity = document.documentList.firstOrNull { it.name == "anotherEntity" }

        assertEquals("newAttribute1", motherEntity?.attributeList?.get(0)?.name)
        assertEquals("value1", motherEntity?.attributeList?.get(0)?.value)
        assertEquals("attribute2", motherEntity?.attributeList?.get(1)?.name)
        assertEquals("value2", motherEntity?.attributeList?.get(1)?.value)

        assertEquals("attribute1", anotherEntity?.attributeList?.get(0)?.name)
        assertEquals("value1", anotherEntity?.attributeList?.get(0)?.value)
        assertEquals("attribute2", anotherEntity?.attributeList?.get(1)?.name)
        assertEquals("value2", anotherEntity?.attributeList?.get(1)?.value)

        // Attempting to rename attribute in non-existing entity should not affect the document
        document.renameAttribute("nonExistingEntity", "attribute1", "shouldNotChange")

        // Check if nothing changed
        assertEquals("newAttribute1", motherEntity?.attributeList?.get(0)?.name)
        assertEquals("value1", motherEntity?.attributeList?.get(0)?.value)
        assertEquals("attribute2", motherEntity?.attributeList?.get(1)?.name)
        assertEquals("value2", motherEntity?.attributeList?.get(1)?.value)

        assertEquals("attribute1", anotherEntity?.attributeList?.get(0)?.name)
        assertEquals("value1", anotherEntity?.attributeList?.get(0)?.value)
        assertEquals("attribute2", anotherEntity?.attributeList?.get(1)?.name)
        assertEquals("value2", anotherEntity?.attributeList?.get(1)?.value)
    }


    // 9. Remove entities globally in the document

    @Test
    fun testRemoveEntityGlobally() {
        // Add entities to the document
        document.addEntity(Entity("entity1"))
        document.addEntity(Entity("entity2"))
        document.addEntity(Entity("entity3"))

        // Check initial size
        assertEquals(3, document.documentList.size)

        // Remove an existing entity
        document.removeEntityGlobally("entity2")
        assertEquals(2, document.documentList.size)
        assertEquals("entity1", document.documentList[0].name)
        assertEquals("entity3", document.documentList[1].name)

        // Remove another existing entity
        document.removeEntityGlobally("entity1")
        assertEquals(1, document.documentList.size)
        assertEquals("entity3", document.documentList[0].name)
    }


    // 10. Remove attributes globally in the document

    @Test
    fun testRemoveAttributeGlobally() {
        // Add entities to the document
        document.addEntity(Entity("motherEntity"))
        document.addEntity(Entity("anotherEntity"))

        // Add attributes to entities
        document.addAttributeToEntity("motherEntity", "attribute1", "value1")
        document.addAttributeToEntity("anotherEntity", "attribute2", "value2")

        // Check initial size
        assertEquals(2, document.documentList.size)

        // Remove an existing attribute globally
        document.removeAttributeGlobally("motherEntity", "attribute1")
        assertEquals(2, document.documentList.size) // Size remains the same, as anotherEntity has not been affected
        assertEquals(0, document.documentList[0].attributeList.size) // motherEntity's attributeList should be empty now
        assertEquals(
            1,
            document.documentList[1].attributeList.size
        ) // anotherEntity's attributeList should remain unaffected

        // Attempting to remove a non-existing attribute should not affect the document
        document.removeAttributeGlobally("nonExistingEntity", "attribute1")
        assertEquals(2, document.documentList.size) // Size remains the same
    }


    // Interrogate document with XML expressions
    @Test
    fun testXPath() {
        // Create a new Document
        val document = Document(version = "1.0", encoding = "UTF-8")

        // Create entities

        // Lvl 1
        val planoEntity = Entity("plano")
        // Add root entity to the document
        document.addEntity(planoEntity)

        // Lvl 2
        val cursoEntity = Entity("curso", "Mestrado em Engenharia Informática")
        planoEntity.addDaughterEntity(cursoEntity)

        val fucEntity = Entity("fuc")
        fucEntity.addAttribute("codigo", "M4310")
        planoEntity.addDaughterEntity(fucEntity)


        // Lvl 3
        val nomeEntity = Entity("nome", "Programação Avançada")
        fucEntity.addDaughterEntity(nomeEntity)
        val ectsEntity = Entity("ects", "6.0")
        fucEntity.addDaughterEntity(ectsEntity)
        val avaliacaoEntity = Entity("avaliacao")
        fucEntity.addDaughterEntity(avaliacaoEntity)

        // Lvl 4
        val componenteEntity1 = Entity("componente")
        componenteEntity1.addAttribute("nome", "Quizzes")
        componenteEntity1.addAttribute("peso", "20%")
        avaliacaoEntity.addDaughterEntity(componenteEntity1)

        val componenteEntity2 = Entity("componente")
        componenteEntity2.addAttribute("nome", "Projeto")
        componenteEntity2.addAttribute("peso", "80%")
        avaliacaoEntity.addDaughterEntity(componenteEntity2)


        // Expression 1: plano/fuc/avaliacao
        val result1 = document.queryXPath("plano/fuc/avaliacao")
        println("Result 1: ${result1.map { it.name }}")
        assertEquals(3, result1.size)
        assertTrue(result1.any { it.name == "avaliacao" })
        assertTrue(result1.any { it.name == "componente" && it.attributeList.any { it.name == "nome" && it.value == "Quizzes" } })
        assertTrue(result1.any { it.name == "componente" && it.attributeList.any { it.name == "nome" && it.value == "Projeto" } })

        // Expression 2: fuc/avaliacao
        val result2 = document.queryXPath("fuc/avaliacao")
        println("Result 2: ${result2.map { it.name }}")
        assertEquals(3, result2.size)
        assertTrue(result2.any { it.name == "avaliacao" })
        assertTrue(result2.any { it.name == "componente" && it.attributeList.any { it.name == "nome" && it.value == "Quizzes" } })
        assertTrue(result2.any { it.name == "componente" && it.attributeList.any { it.name == "nome" && it.value == "Projeto" } })

        // Expression 3: fuc/avaliacao/componente
        val result3 = document.queryXPath("fuc/avaliacao/componente")
        println("Result 3: ${result3.map { it.name }}")
        assertEquals(2, result3.size)
        assertTrue(result3.all { it.name == "componente" })
    }
}


// Phase 2
class XmlMappingTest {

    // Test mapping with XmlEntityName, XmlAttribute, XmlEntity and XmlIgnore

    @XmlEntityName("componente")
    data class ComponenteAvaliacao(
        @XmlAttribute val nome: String,
        @XmlAttribute val peso: Int
    )

    @XmlEntityName("fuc")
    data class FUC(
        @XmlAttribute val codigo: String,
        @XmlEntity val nome: String,
        @XmlEntity val ects: Double,
        @XmlIgnore val observacoes: String,
        @XmlEntity val avaliacao: List<ComponenteAvaliacao>
    )

    @Test
    fun testSingleComponentMapping() {

        // Create a single ComponenteAvaliacao object
        val componente = ComponenteAvaliacao("Quizzes", 20)

        // Use the function to convert the object to an XML entity
        val entity = mapObjectToXmlEntity(componente)

        // Expected structure of the entity
        val expectedEntity = Entity("componente").apply {
            addAttribute("nome", "Quizzes")
            addAttribute("peso", "20")
        }

        /// Verify that the generated entity matches the expected entity
        assertEquals(expectedEntity.name, entity.name)

        // Compare attribute lists by content
        val expectedAttributes = expectedEntity.attributeList.map { it.name to it.value }.toSet()
        val actualAttributes = entity.attributeList.map { it.name to it.value }.toSet()
        assertEquals(expectedAttributes, actualAttributes)

    }


    // Test for a more complex entity
    @Test
    fun testXmlMapping() {
        // Create some objects based on our blueprints
        val componente1 = ComponenteAvaliacao("Quizzes", 20)
        val componente2 = ComponenteAvaliacao("Projeto", 80)
        val fuc = FUC("M4310", "Programação Avançada", 6.0, "la la...", listOf(componente1, componente2))

        // Use the function to convert the object to an XML entity
        val entity = mapObjectToXmlEntity(fuc)

        // Expected structure of the entity
        val expectedEntity = Entity("fuc").apply {
            addAttribute("codigo", "M4310")
            addDaughterEntity(Entity("nome", "Programação Avançada"))
            addDaughterEntity(Entity("ects", "6.0"))
            val avaliacaoEntity = Entity("avaliacao").apply {
                addDaughterEntity(Entity("componente").apply {
                    addAttribute("nome", "Quizzes")
                    addAttribute("peso", "20")
                })
                addDaughterEntity(Entity("componente").apply {
                    addAttribute("nome", "Projeto")
                    addAttribute("peso", "80")
                })
            }
            addDaughterEntity(avaliacaoEntity)
        }

        // Verify that the generated entity matches the expected entity
        assertEquals(expectedEntity.name, entity.name)

        // Compare attribute lists by content
        val expectedAttributes = expectedEntity.attributeList.map { it.name to it.value }.toSet()
        val actualAttributes = entity.attributeList.map { it.name to it.value }.toSet()
        assertEquals(expectedAttributes, actualAttributes)

        // Compare daughter entities by name and attributes, ignoring order
        val expectedDaughterEntities = expectedEntity.daughterEntities.map { it.name to it }.toMap()
        val actualDaughterEntities = entity.daughterEntities.map { it.name to it }.toMap()

        assertEquals(expectedDaughterEntities.keys, actualDaughterEntities.keys)

        expectedDaughterEntities.forEach { (name, expectedDaughter) ->
            val actualDaughter = actualDaughterEntities[name]
            assertEquals(expectedDaughter.name, actualDaughter?.name)

            val expectedDaughterAttributes = expectedDaughter.attributeList.map { it.name to it.value }.toSet()
            val actualDaughterAttributes = actualDaughter?.attributeList?.map { it.name to it.value }?.toSet()
            assertEquals(expectedDaughterAttributes, actualDaughterAttributes)

            // Recursively compare the nested daughter entities
            compareDaughterEntities(expectedDaughter, actualDaughter!!)
        }
    }

    private fun compareDaughterEntities(expected: Entity, actual: Entity) {
        val expectedDaughterEntities = expected.daughterEntities.map { it.name to it }.toMap()
        val actualDaughterEntities = actual.daughterEntities.map { it.name to it }.toMap()

        assertEquals(expectedDaughterEntities.keys, actualDaughterEntities.keys)

        expectedDaughterEntities.forEach { (name, expectedDaughter) ->
            val actualDaughter = actualDaughterEntities[name]
            assertEquals(expectedDaughter.name, actualDaughter?.name)

            val expectedDaughterAttributes = expectedDaughter.attributeList.map { it.name to it.value }.toSet()
            val actualDaughterAttributes = actualDaughter?.attributeList?.map { it.name to it.value }?.toSet()
            assertEquals(expectedDaughterAttributes, actualDaughterAttributes)

            // Recursively compare the nested daughter entities
            compareDaughterEntities(expectedDaughter, actualDaughter!!)
        }
    }

    // Test XmlString

    // Define a transformation that adds a percentage sign
    class AddPercentage : StringTransformer {
        override fun transform(value: Any): String {
            return "$value%"
        }
    }

    @XmlEntityName("componente")
    data class ComponenteAvaliacao2(
        @XmlAttribute val nome: String,
        @XmlString(AddPercentage::class)
        @XmlAttribute val peso: Int
    )

    @Test
    fun testXmlStringTransformation() {
        // Create a single ComponenteAvaliacao2 object
        val componente = ComponenteAvaliacao2("Quizzes", 20)

        // Use the function to convert the object to an XML entity
        val entity = mapObjectToXmlEntity(componente)

        // Expected structure of the entity
        val expectedEntity = Entity("componente").apply {
            addAttribute("nome", "Quizzes")
            addAttribute("peso", "20%")
        }

        // Verify that the generated entity matches the expected entity
        assertEquals(expectedEntity.name, entity.name)

        // Compare attribute lists by content
        val expectedAttributes = expectedEntity.attributeList.map { it.name to it.value }.toSet()
        val actualAttributes = entity.attributeList.map { it.name to it.value }.toSet()
        assertEquals(expectedAttributes, actualAttributes)

    }


    // Test XmlAdapter

    class AddIdPostProcessor : PostProcessor {
        private var idCounter = 0

        override fun process(entity: Entity) {
            entity.addAttribute("id", (++idCounter).toString())
            entity.daughterEntities.forEach { process(it) }
        }
    }

    @XmlEntityName("componente")
    @XmlAdapter(AddIdPostProcessor::class)
    data class ComponenteAvaliacao3(
        @XmlAttribute val nome: String,
        @XmlString(AddPercentage::class)
        @XmlAttribute val peso: Int
    )


    @Test
    fun testXmlAdapter() {
        // Create a single ComponenteAvaliacao object
        val componente = ComponenteAvaliacao3("Quizzes", 20)

        // Use the function to convert the object to an XML entity
        val entity = mapObjectToXmlEntity(componente)

        // Expected structure of the entity
        val expectedEntity = Entity("componente").apply {
            addAttribute("nome", "Quizzes")
            addAttribute("peso", "20%")
            addAttribute("id", "1")
        }

        // Verify that the generated entity matches the expected entity
        assertEquals(expectedEntity.name, entity.name)

        // Compare attribute lists by content
        val expectedAttributes = expectedEntity.attributeList.map { it.name to it.value }.toSet()
        val actualAttributes = entity.attributeList.map { it.name to it.value }.toSet()
        assertEquals(expectedAttributes, actualAttributes)

    }


}


