# XML Manipulation and Query API Tutorial

## Part 1 - XML Representation

## Table of Contents

1. [Introduction](#introduction)
2. [Add and Remove Entities](#add-and-remove-entities)
    - [Add an Entity](#add-an-entity)
    - [Remove an Entity](#remove-an-entity)
3. [Add, Remove and Edit Attributes](#add-remove-and-edit-attributes)
    - [Add an Attribute](#add-an-attribute)
    - [Remove an Attribute](#remove-an-attribute)
    - [Edit an Attribute](#edit-an-attribute)
4. [Access Mother and Daughter Entities](#access-mother-and-daughter-entities)
    - [Add Daughter Entity](#add-daughter-entity)
    - [Access Mother Entity](#access-mother-entity)
    - [Access Daughter Entities](#access-daughter-entities)
5. [Pretty Print String Format and Write to File](#pretty-print-string-format-and-write-to-file)
    - [Convert Document to XML String](#convert-document-to-xml-string)
6. [Add Attributes Globally to the Document](#add-attributes-globally-to-the-document)
    - [Add Attribute to Entity](#add-attribute-to-entity)
7. [Rename Entities Globally in the Document](#rename-entities-globally-in-the-document)
    - [Rename an Entity](#rename-an-entity)
8. [Rename Attributes Globally in the Document](#rename-attributes-globally-in-the-document)
    - [Rename an Attribute](#rename-an-attribute)
9. [Remove Entities Globally in the Document](#remove-entities-globally-in-the-document)
    - [Remove an Entity Globally](#remove-an-entity-globally)
10. [Remove Attributes Globally in the Document](#remove-attributes-globally-in-the-document)
    - [Remove an Attribute Globally](#remove-an-attribute-globally)
11. [Interrogate Document with XML Expressions](#interrogate-document-with-xml-expressions)
    - [Query with XPath](#query-with-xpath)

## Introduction

This tutorial covers various functionalities of the XML Manipulation API in Kotlin. The user will learn how to add and remove entities, manipulate attributes, access mother and daughter entities, and query with XML expressions.

## Add and Remove Entities

### Add an Entity

To add an entity to a document, create an instance of `Entity` and use the `addEntity` method.

```kotlin
val entity1 = Entity(name="entity1")
document.addEntity(entity1)
```

### Remove an Entity

To remove an entity from a document, use the removeEntity method with the entity name.

```kotlin
val entity1 = Entity(name="entity1")
document.removeEntity(entityName="entity1")
```

## Add, Remove and Edit Attributes

### Add an Attribute

To add an attribute to an entity, use the addAttribute method.

```kotlin
val entity = Entity(name="entity")
entity.addAttribute(attributeName="attribute1", attributeValue="value1")
```

### Remove an Attribute

To remove an attribute from an entity, use the removeAttribute method.

```kotlin
val entity = Entity(name="entity")
entity.removeAttribute(attributeName="attribute1")
```

### Edit an Attribute

To edit an attribute of an entity, use the editAttribute method.

```kotlin
val entity = Entity(name="entity")
entity.editAttribute(oldAttributeName="attribute1", newAttributeName="newAttribute1", newAttributeValue="newValue1")
```

## Access Mother and Daughter Entities

### Add Daughter Entity

To associate a daughter entity to a given entity, use the addDaughterEntity method. 

```kotlin
val motherEntity = Entity("motherEntity")
val daughterEntity = Entity("daughterEntity")

motherEntity.addDaughterEntity(daughterEntity)
```

### Access Mother Entity

To access the mother entity of a given entity, use the accessMotherEntity method.

```kotlin
daughterEntity.accessMotherEntity()
```

### Access Daughter Entities

To access the daughter entities of a given entity, use the accessDaughterEntities method.

```kotlin
motherEntity.accessDaughterEntities()
```

## Pretty Print String Format and Write to File

### Convert Document to XML String

To convert a document to an XML string, use the toXmlString method. To write the produced string into a file, pass the argument "filePath".

```kotlin
// Does not write to a file
document.toXmlString()

// Writes to file: "xmlStringOutput.txt"
document.toXmlString(filePath="xmlStringOutput.txt")
```

## Add Attributes Globally to the Document

### Add Attribute to Entity

To add an attribute to a specific entity in the document, use the addAttributeToEntity method.

```kotlin
document.addAttributeToEntity(motherEntityName="motherEntity", attributeName="attribute1", attributeValue="value1")
```

## Rename Entities Globally in the Document

### Rename an Entity

To rename an entity globally in the document, use the renameEntity method.

```kotlin
document.renameEntity(oldName="oldName1", newName="newName1")
```

## Rename Attributes Globally in the Document

### Rename an Attribute

To rename an attribute globally in the document, use the renameAttribute method.

```kotlin
document.renameAttribute(motherEntityName="motherEntity", oldAttributeName="attribute1", newAttributeName="newAttribute1")
```

## Remove Entities Globally in the Document

### Remove an Entity Globally

To remove an entity globally in the document, use the removeEntityGlobally method.

```kotlin
document.removeEntityGlobally(entityName="entity1")
```

## Remove Attributes Globally in the Document

### Remove an Attribute Globally

To remove an attribute globally in the document, use the removeAttributeGlobally method.

```kotlin
document.removeAttributeGlobally(motherEntityName="motherEntity", attributeName="attribute1")
```

## Interrogate Document with XML Expressions

### Query with XPath

To query the document with an XML expression, use the queryXPath method.

```kotlin
document.queryXPath("plano/fuc/avaliacao")
```