# XML Manipulation and Query API Tutorial

## Part 2 - XML Mapping

## Table of Contents

1. [Introduction](#introduction)
2. [Defining XML Entities and Attributes](#defining-xml-entities-and-attributes)
3. [Mapping Objects to XML Entities](#mapping-objects-to-xml-entities)
4. [Using `@XmlString` for Transformation](#using-xmlstring-for-transformation)
5. [Using `@XmlAdapter` for Post-Processing](#using-xmladapter-for-post-processing)


## Introduction

This tutorial will guide the user through using a Kotlin-based API for manipulating XML-like structures. The API provides annotations for defining XML entities and attributes, and functions for converting objects to XML entities.


## Defining XML Entities and Attributes

You can define XML entities and attributes using annotations. Here's a list of the annotations provided:

- `@XmlEntityName`: Defines the name of an XML entity.
- `@XmlAttribute`: Marks a property as an XML attribute.
- `@XmlEntity`: Marks a property as a nested XML entity.
- `@XmlIgnore`: Ignores a property during XML conversion.
- `@XmlString`: Applies a string transformation to a property.
- `@XmlAdapter`: Applies a post-processing adapter to an entity.

### Examples

```kotlin
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
```

## Mapping Objects to XML Entities
Use the mapObjectToXmlEntity function to convert an object to an XML entity. The function uses reflection to read the annotations and properties of the object.

Examples:

```kotlin
@XmlEntityName("componente")
data class ComponenteAvaliacao(
    @XmlAttribute val nome: String,
    @XmlAttribute val peso: Int
)

val componente = ComponenteAvaliacao(nome="Quizzes", peso=20)
val entity1 = mapObjectToXmlEntity(componente)

-----

@XmlEntityName("fuc")
data class FUC(
    @XmlAttribute val codigo: String,
    @XmlEntity val nome: String,
    @XmlEntity val ects: Double,
    @XmlIgnore val observacoes: String,
    @XmlEntity val avaliacao: List<ComponenteAvaliacao>
)
    
val fuc = FUC(codigo="M4310", nome="Programação Avançada", ects=6.0,
                observacoes="la la...",
                avaliacao=listOf(
                    ComponenteAvaliacao("Quizzes", "20"),
                    ComponenteAvaliacao("Projeto", "80")
                )
            )
    
val entity2 = mapObjectToXmlEntity(fuc)
```

## Using `@XmlString` for Transformation

Define a transformation class that implements the `StringTransformer` interface. This class will modify the XML attribute values during the mapping process.

```kotlin
// Define a transformation that adds a percentage sign
class AddPercentage : StringTransformer {
    override fun transform(value: Any): String {
        return "$value%"
    }
}
```

Having defined a data class, the user can apply the @XmlString annotation to specify the transformation for the chosen XML attribute. In the following example, a percentage sign is added to the 'peso' attribute:

```kotlin
@XmlEntityName("componente")
data class ComponenteAvaliacao2(
@XmlAttribute val nome: String,
@XmlString(AddPercentage::class)
@XmlAttribute val peso: Int
)
```

## Using `@XmlAdapter` for Post-Processing

Define a transformation class that implements the `PostProcessor` interface. This class will modify the XML attribute values after the mapping process.

```kotlin
// Define a transformation that adds an 'id' attribute to all entities
class AddIdPostProcessor : PostProcessor {
    private var idCounter = 0

    override fun process(entity: Entity) {
        entity.addAttribute("id", (++idCounter).toString())
        entity.daughterEntities.forEach { process(it) }
    }
}
```

Define a data class and apply the @XmlAdapter annotation to specify the post-processor for the XML entity. In the following example, an 'id' attribute will be added to the defined entity:

```kotlin
@XmlEntityName("componente")
@XmlAdapter(AddIdPostProcessor::class)
data class ComponenteAvaliacao3(
@XmlAttribute val nome: String,
@XmlString(AddPercentage::class)
@XmlAttribute val peso: Int
)
```

