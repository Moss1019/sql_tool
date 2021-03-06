

import java.util.Map;
import java.util.HashMap;

public class EntityGenerator extends Generator {
  private Database db;

  private String classTmpl;
  private String classJoinedTmpl;
  private String fieldTmpl;
  private String getterTmpl;
  private String setterTmpl;
  private String primaryFieldTmpl;
  private String namedDeleteTmpl;
  private String namedInsertTmpl;
  private String namedUpdateTmpl;
  private String namedSelectAllTmpl;
  private String namedSelectByPkTmpl;
  private String namedSelectUniqueTmpl;
  private String namedSelectOfParentTmpl;
  private String namedDeleteJoinedTmpl;
  private String storedProcedureParameterTmpl;

  public EntityGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public Map<String, String> generate() {
    Map<String, String> entities = new HashMap<>();
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsJoined() || t.getIsLooped();
      StringBuilder b = new StringBuilder();
      String cls = (currentLoopedOrJoined ? classJoinedTmpl : classTmpl)
        .replace("{namedstoredprocedures}", generateNamedProcedures(t))
        .replace("{getters}", generateGetters(t))
        .replace("{setters}", generateSetters(t))
        .replace("{packagename}", db.getPackageName())
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{tablename}", t.getName());
      if(currentLoopedOrJoined) {
        b
        .append(cls
          .replace("{primaryfield1}", primaryFieldTmpl
            .replace("{columnname}", t.getPrimaryColumn().getName())
            .replace("{columnnamecamel}", t.getPrimaryColumn().getCamelName()))
          .replace("{primaryfield2}", primaryFieldTmpl
            .replace("{columnname}", t.getJoinedColumn().getName())
            .replace("{columnnamecamel}", t.getJoinedColumn().getCamelName()))
          .replace("{primarykey1camel}", t.getPrimaryColumn().getCamelName())
          .replace("{primarykey2camel}", t.getJoinedColumn().getCamelName()));
      } else {
        b
        .append(cls
          .replace("{primaryfield}", generatePrimaryField(t))
          .replace("{fields}", generateFields(t)));
      }
      entities.put(t.getPascalName(), b.toString());
    }
    return entities;
  }

  private String generateNamedProcedures(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(currentLoopedOrJoined ? generateJoinedDelete(t) : generateNamedDelete(t))
    .append(generateNamedInsert(t))
    .append(generateNamedSelectAll(t))
    .append(generateNamedSelectByPk(t))
    .append(generateNamedSelectUnique(t))
    .append(generateNamedUpdate(t))
    .append(generateNamedSelectOfParent(t));
    return b.toString();
  }

  private String generateNamedDelete(Table t) {
    StringBuilder b = new StringBuilder();
    b.append(namedDeleteTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{primarykeyname}", t.getPrimaryColumn().getName()));
    return b.toString();
  }

  private String generateJoinedDelete(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(namedDeleteJoinedTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{joinedkeyname}", t.getJoinedColumn().getName())
      .replace("{primarykeyname}", t.getPrimaryColumn().getName()));
    return b.toString();
  }

  private String generateNamedInsert(Table t) {
    StringBuilder b = new StringBuilder();
    StringBuilder parameters = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getNonPrimaryColumns()) {
      parameters
      .append(storedProcedureParameterTmpl
        .replace("{javawrappertype}", DataTypeUtil.resolveWrapperType(c.getDataType()))
        .replace("{columnname}", c.getName()));
      if(colIndex++ < t.getNonPrimaryColumns().size() - 1) {
        parameters.append(",\n");
      }
    }
    b
    .append(namedInsertTmpl.replace("{parameters}", parameters.toString()));
    return b.toString();
  }

  private String generateNamedSelectAll(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(namedSelectAllTmpl
      .replace("{tablenamepascal}", t.getPascalName()));
    return b.toString();
  }

  private String generateNamedSelectByPk(Table t) {
    StringBuilder b = new StringBuilder();
    b.append(namedSelectByPkTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{primarykeyname}", t.getPrimaryColumn().getName()));
    return b.toString();
  }

  private String generateNamedSelectUnique(Table t) {
    StringBuilder b = new StringBuilder();
    for(Column c: t.getUniqueColumns()) {
      b
      .append(namedSelectUniqueTmpl
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{columnnamepascal}", c.getPascalName())
        .replace("{columntype}", DataTypeUtil.resolveWrapperType(c.getDataType()))
        .replace("{columnname}", c.getName()));   
    }
    return b.toString();
  }

  private String generateNamedSelectOfParent(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table pt: t.getParentTables()) {
      b.append(namedSelectOfParentTmpl
        .replace("{parenttablenamepascal}", pt.getPascalName())
        .replace("{childtablenamepascal}", t.getPascalName())
        .replace("{parentprimarykey}", pt.getPrimaryColumn().getName())
        .replace("{childtablename}", t.getName()));
    }
    return b.toString();
  }

  private String generateNamedUpdate(Table t) {
    StringBuilder parameters = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      parameters.append(storedProcedureParameterTmpl
        .replace("{javawrappertype}", DataTypeUtil.resolveWrapperType(c.getDataType()))
        .replace("{columnname}", c.getName()));
      if(colIndex++ < t.getColumns().size() - 1) {
        parameters.append(",\n");
      }
    }
    StringBuilder b = new StringBuilder();
    b.append(namedUpdateTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{parameters}", parameters.toString()));
    return b.toString();
  }

  private String generatePrimaryField(Table t) {
    StringBuilder b = new StringBuilder();
    b.append(primaryFieldTmpl
      .replace("{columnname}", t.getPrimaryColumn().getName())
      .replace("{columnnamecamel}", t.getPrimaryColumn().getCamelName()));
    return  b.toString();
  }

  private String generateFields(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getNonPrimaryColumns()) {
      b
      .append(fieldTmpl
        .replace("{columnname}", c.getName())
        .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
        .replace("{columnnamecamel}", c.getCamelName()));
      if(colIndex++ < t.getNonPrimaryColumns().size() - 1) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  private String generateGetters(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      b
      .append(getterTmpl
        .replace("{columnnamepascal}", c.getPascalName())
        .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
        .replace("{columnnamecamel}", c.getCamelName()));
      if(colIndex++ < t.getColumns().size() - 1) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  private String generateSetters(Table t) {
    StringBuilder b = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      b
      .append(setterTmpl
        .replace("{columnnamepascal}", c.getPascalName())
        .replace("{javatype}", DataTypeUtil.resolvePrimitiveType(c.getDataType()))
        .replace("{columnnamecamel}", c.getCamelName()));
      if(colIndex++ < t.getColumns().size() - 1) {
        b.append("\n");
      }
    }
    return b.toString();
  }

  private void loadTemplates() {
    classTmpl = loadTemplate("../templates/entity", "class");
    classJoinedTmpl = loadTemplate("../templates/entity", "classjoined");
    fieldTmpl  = loadTemplate("../templates/entity", "field");
    namedDeleteTmpl = loadTemplate("../templates/entity", "nameddelete");
    namedInsertTmpl = loadTemplate("../templates/entity", "namedinsert");
    namedUpdateTmpl = loadTemplate("../templates/entity", "namedupdate");
    namedSelectAllTmpl = loadTemplate("../templates/entity", "namedselectall");
    namedSelectByPkTmpl = loadTemplate("../templates/entity", "namedselectpk");
    namedSelectUniqueTmpl = loadTemplate("../templates/entity", "namedselectunique");
    namedSelectOfParentTmpl = loadTemplate("../templates/entity", "namedselectofparent");
    namedDeleteJoinedTmpl = loadTemplate("../templates/entity", "nameddeletejoined");
    storedProcedureParameterTmpl = loadTemplate("../templates/entity", "storedprocedureparamter");
    primaryFieldTmpl = loadTemplate("../templates/entity", "primaryfield");
    getterTmpl = loadTemplate("../templates", "getter");
    setterTmpl = loadTemplate("../templates", "setter");
  }
}
