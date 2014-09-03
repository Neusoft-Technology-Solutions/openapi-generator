package com.wordnik.swagger.codegen.languages;

import com.wordnik.swagger.util.Json;
import com.wordnik.swagger.codegen.*;
import com.wordnik.swagger.models.properties.*;

import java.util.*;
import java.io.File;

public class ObjcClientCodegen extends DefaultCodegen implements CodegenConfig {
  protected Set<String> foundationClasses = new HashSet<String>();

  public ObjcClientCodegen() {
    super();
    outputFolder = "generated-code/objc";
    modelTemplateFiles.put("model-header.mustache", ".h");
    modelTemplateFiles.put("model-body.mustache", ".m");
    apiTemplateFiles.put("api-header.mustache", ".h");
    apiTemplateFiles.put("api-body.mustache", ".m");
    templateDir = "objc";
    modelPackage = "";

    defaultIncludes = new HashSet<String>(
      Arrays.asList("bool",
        "int",
        "NSString",
        "NSObject", 
        "NSArray",
        "NSNumber")
      );
    languageSpecificPrimitives = new HashSet<String>(
      Arrays.asList(
        "NSNumber",
        "NSString",
        "NSObject",
        "bool")
      );

    reservedWords = new HashSet<String>(
      Arrays.asList(
        "void", "char", "short", "int", "void", "char", "short", "int",
        "long", "float", "double", "signed", "unsigned", "id", "const",
        "volatile", "in", "out", "inout", "bycopy", "byref", "oneway",
        "self", "super"
      ));

    typeMapping = new HashMap<String, String>();
    typeMapping.put("enum", "NSString");
    typeMapping.put("date", "SWGDate");
    typeMapping.put("dateTime", "SWGDate");
    // typeMapping.put("Date", "SWGDate");
    typeMapping.put("boolean", "NSNumber");
    typeMapping.put("string", "NSString");
    typeMapping.put("integer", "NSNumber");
    typeMapping.put("int", "NSNumber");
    typeMapping.put("float", "NSNumber");
    typeMapping.put("long", "NSNumber");
    typeMapping.put("double", "NSNumber");
    typeMapping.put("Array", "NSArray");
    // typeMapping.put("array", "NSArray");
    typeMapping.put("List", "NSArray");
    typeMapping.put("object", "NSObject");

    importMapping = new HashMap<String, String> ();
    importMapping.put("Date", "SWGDate");

    foundationClasses = new HashSet<String> (
      Arrays.asList(
        "NSNumber",
        "NSObject",
        "NSString")
      );
  }

  @Override
  public String getSwaggerType(Property p) {
    String swaggerType = super.getSwaggerType(p);
    String type = null;
    if(typeMapping.containsKey(swaggerType)) {
      type = typeMapping.get(swaggerType);
      if(languageSpecificPrimitives.contains(type) && !foundationClasses.contains(type))
        return toModelName(type);
    }
    else
      type = swaggerType;
    return toModelName(type);
  }

  @Override
  public String getTypeDeclaration(Property p) {
    String swaggerType = getSwaggerType(p);
    if(languageSpecificPrimitives.contains(swaggerType) && !foundationClasses.contains(swaggerType))
      return toModelName(swaggerType);
    else
      return swaggerType + "*";
  }

  @Override
  public String toModelName(String type) {
    if(typeMapping.keySet().contains(type) ||
      foundationClasses.contains(type) ||
      importMapping.values().contains(type) ||
      defaultIncludes.contains(type) ||
      languageSpecificPrimitives.contains(type)) {
      return Character.toUpperCase(type.charAt(0)) + type.substring(1);
    }
    else {
      return "SWG" + Character.toUpperCase(type.charAt(0)) + type.substring(1);
    }
  }

  @Override
  public String toModelImport(String name) {
    name = name + ".h";
    if("".equals(modelPackage()))
      return name;
    else
      return modelPackage() + "." + name;
  }

  @Override
  public String apiFileFolder() {
    return outputFolder + File.separator + "client";
  }

  @Override
  public String modelFileFolder() {
    return outputFolder + File.separator + "client";
  }

  @Override
  public String toModelFilename(String name) {
    return "SWG" + initialCaps(name);
  }

  public String toApiFilename(String name) {
    return "SWG" + initialCaps(name);
  }

  @Override
  public String toVarName(String name) {
    String paramName = name.replaceAll("[^a-zA-Z0-9_]","");
    if(paramName.startsWith("new") || reservedWords.contains(paramName)) {
      return escapeReservedWord(paramName);
    }
    else
      return paramName;
  }

  public String escapeReservedWord(String name) {
    return "_" + name;
  }
}