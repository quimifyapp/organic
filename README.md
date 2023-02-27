# Quimify Organic

## Overview

Quimify Organic is a high-school level organic nomenclature module that provides name-to-structure and structure-to-name conversion tools, currently supporting Spanish. It is used in [Quimify](https://quimify.com/).

## Usage Examples

### Name-to-structure conversion

Build it:

```java
String name = "ácido 2-metilpropanoico";
Optional<Organic> organic = OrganicFactory.getFromName(name);
```

Result:

```java
organic.get().getStructure(); // "CH3-CH(CH3)-COOH"
organic.get().getSmiles(); // "CC(C(=O)O)C"
```
  
### Structure-to-name conversion

Build it:  

```java
OpenChain openChain = new Simple(); // C

openChain = openChain.bond(Group.hydrogen); // CH≡

Substituent methyl = Substituent.radical(1); // CH3
openChain = openChain.bond(methyl); // CH(CH3)=
openChain = openChain.bond(methyl); // CH(CH3)2-
        
openChain.bondCarbon(); // CH(CH3)2-C≡
        
openChain = openChain.bond(Group.acid); // CH(CH3)2-COOH 
```

Result:

```java
if(openChain.isDone()) {
    openChain.correct(); // CH3-CH(CH3)-COOH
    openChain.getStructure(); // "CH3-CH(CH3)-COOH"
    openChain.getName(); // "ácido 2-metilpropanoico"
}
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

## License?

Do whatever you want with this code.

However, please be aware that our code relies on open-source dependencies, each with their own licenses and usage terms. Non-compliance with their licenses could have legal consequences.

