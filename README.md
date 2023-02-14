# Quimify Organic

## Overview

Quimify Organic is a high-school level organic nomenclature module in [Quimify](https://quimify.com/). It provides name-to-structure and structure-to-name conversion tools, currently written in Spanish.

## Usage Examples

### Name-to-structure conversion

```java
Optional<Organic> organic = OrganicFactory.getFromName("ácido 2-metilpropanoico");

organic.get().getStructure(); // CH3-CH(CH3)-COOH
organic.get().getSmiles(); // CC(C(=O)O)C
```
  
### Structure-to-name conversion
  
```java
OpenChain openChain = new Simple();
openChain.getStructure(); // C

openChain = openChain.bond(Group.hydrogen);
openChain.getStructure(); // CH≡

Substituent methyl = Substituent.radical(1);
methyl.toString(); // CH3

openChain = openChain.bond(methyl);
openChain.getStructure(); // CH(CH3)=

openChain = openChain.bond(methyl);
openChain.getStructure(); // CH(CH3)2-

openChain.bondCarbon();
openChain.getStructure(); // CH(CH3)2-C≡

openChain = openChain.bond(Group.acid);
openChain.getStructure(); // CH(CH3)2-COOH
```

To check if the molecule is done:

```java
if(openChain.isDone()) {
  // ...
}
```

Finally:

```java
openChain.correct();
openChain.getStructure(); // CH3-CH(CH3)-COOH
openChain.getName(); // ácido 2-metilpropanoico
```

## How it works
  
![organic.png](doc/organic.png?raw=true "Flowchart")

## Usage
  
Clone the repository and run `NameToStructure`, `StructureToName` or `RandomStructureToName` using Java 11.

## Installation
  
To use Quimify Organic in your project, you can generate a JAR file with dependencies by running the Gradle task `shadowJar` with the command `gradle shadowjar`. The JAR file will be located at `build/libs`.

Alternatively, download the latest [release](https://github.com/quimifyapp/organic/releases) available.

## Dependencies
  
Quimify Organic uses the `quimify-opsin-es` module from [quimifyapp/opsin](https://github.com/quimifyapp/opsin).
