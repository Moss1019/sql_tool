
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class SqlGenerator extends Generator {
  private Database db;

  private String createTableTmpl;

  private String deleteJoinedTmpl;
  private String deleteByPkTmpl;
  private String selectAllTmpl;
  private String selectByPkTmpl;
  private String selectByUniqueTmpl;
  private String selectLoopedTmpl;
  private String selectJoinedTmpl;
  private String selectOfParentTmpl;
  private String insertTmpl;
  private String updateTmpl;

  private String inparamTmpl;
  private String fieldTmpl;
  private String primaryKeyTmpl;
  private String setValueTmpl;
  private String uniqueFieldTmpl;
  private String foreignKeyTmpl;
  private String grantExecTmpl;

  private List<String> procedures;
  private List<String> tables;

  public SqlGenerator(Database db) {
    this.db = db;
    loadTemplates();
    procedures = new ArrayList<>();
    tables = new ArrayList<>();
  }

  public Map<String, String> generate() {
    StringBuilder b = new StringBuilder();
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsJoined() || t.getIsLooped();
      b
      .append(generateCreateTable(t))
      .append(generateInsert(t));
      if(currentLoopedOrJoined) {
        b.append(generateDeleteJoined(t));
        if(t.getIsLooped()) {
          b.append(generateSelectLooped(t));
        } else {
          b.append(generateSelectJoined(t));
        }
      } else {
        b
        .append(generateSelectAll(t))
        .append(generateSelectByPk(t))
        .append(generatorSelectByUnique(t))
        .append(generateUpdate(t))
        .append(generateDeleteByPk(t))
        .append(generateSelectOfParents(t));
      }
      b.append("\n");
    }
    b.append(generateGrants());
    Map<String, String> sql = new HashMap<>();
    sql.put("create", b.toString());
    sql.put("drop", generateDrops());
    return sql;
  }

  private String generateDrops() {
    StringBuilder b = new StringBuilder();
    for(String p : procedures) {
      b.append("drop procedure ").append(p).append(";\n");
    }
    for(String t: tables) {
      b.append("drop table ").append(t).append(";\n");
    }
    return b.toString();
  }

  private String generateCreateTable(Table t) {
    StringBuilder tableFields = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getColumns()) {
      if(c.getIsPrimary()) {
        tableFields.append(primaryKeyTmpl.replace("{primarykey}", c.getName()));
      } else if(c.getIsUnique()) {
        tableFields.append(uniqueFieldTmpl
          .replace("{columnname}", c.getName())
          .replace("{sqldatatype}", DataTypeUtil.resolveSqlType(c.getDataType())));
      } else if (c.getIsForeign()) {
        tableFields.append(foreignKeyTmpl
          .replace("{columnname}", c.getName())
          .replace("{foreigntablename}", c.getForeignKeyTable())
          .replace("{foreigncolumnname}", c.getForeignKeyName()));
      } else {
        tableFields.append(fieldTmpl
          .replace("{columnname}", c.getName())
          .replace("{sqldatatype}", DataTypeUtil.resolveSqlType(c.getDataType())));
      }
      if(colIndex++ < t.getNumColumns() - 1) {
        tableFields.append(",\n\t");
      }
    }
    StringBuilder b = new StringBuilder();
    b
    .append(createTableTmpl
      .replace("{tablename}", t.getName())
      .replace("{tablefields}", tableFields.toString()));
    tables.add(t.getName());
    return b.toString();
  }

  private String generateSelectAll(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(selectAllTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{tablename}", t.getName())
      .replace("{tablename}", t.getName()));
    procedures.add(String.format("sp_selectAll%ss", t.getPascalName()));
    return b.toString();
  }

  private String generateSelectByPk(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(selectByPkTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{tablename}", t.getName())
      .replace("{primarykey}", t.getPrimaryColumn().getName()));
    procedures.add(String.format("sp_select%s", t.getPascalName()));
    return b.toString();
  }

  private String generatorSelectByUnique(Table t) {
    StringBuilder b = new StringBuilder();
    for(Column c: t.getUniqueColumns()) {
      b
      .append(selectByUniqueTmpl
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{colnamepascal}", c.getPascalName())
        .replace("{colname}", c.getName())
        .replace("{coldatatype}", DataTypeUtil.resolveSqlType(c.getDataType()))
        .replace("{tablename}", t.getName()));
      procedures.add(String.format("sp_select%ssBy%s", t.getPascalName(), c.getPascalName()));
    }
    return b.toString();
  }

  private String generateSelectLooped(Table t) {
    return selectLoopedTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{loopedtablenamepascal}", t.getParentTables().get(0).getPascalName())
      .replace("{loopedcolumnname}", t.getParentTables().get(0).getPrimaryColumn().getName())
      .replace("{loopedtablename}", t.getParentTables().get(0).getName())
      .replace("{primarycolumnname}", t.getPrimaryColumn().getName())
      .replace("{tablename}", t.getName());
  }

  private String generateSelectJoined(Table t) {
    return selectJoinedTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{primarytablenamepascal}", t.getParentTables().get(0).getPascalName())
      .replace("{primarycolumnname}", t.getParentTables().get(0).getPrimaryColumn().getName())
      .replace("{secondarytablename}", t.getParentTables().get(1).getName())
      .replace("{secondarycolumnname}", t.getParentTables().get(1).getPrimaryColumn().getName())
      .replace("{tablename}", t.getName());
  }

  private String generateSelectOfParents(Table t) {
    StringBuilder b = new StringBuilder();
    for(Table pt: t.getParentTables()) {
      b
      .append(selectOfParentTmpl
        .replace("{tablenamepascal}", t.getPascalName())
        .replace("{parenttablenamepascal}", pt.getPascalName())
        .replace("{parentprimarykey}", pt.getPrimaryColumn().getName())
        .replace("{childtablename}", t.getName()));
      procedures.add(String.format("sp_selectOf%s", pt.getPascalName()));
    }
    return b.toString();
  }

  private String generateUpdate(Table t) {
    StringBuilder paramList = new StringBuilder();
    StringBuilder setList = new StringBuilder();
    paramList
      .append("\n\t")
      .append(inparamTmpl
        .replace("{columnname}", t.getPrimaryColumn().getName())
        .replace("{sqldatatype}", DataTypeUtil.resolveSqlType(t.getPrimaryColumn().getDataType())))
      .append(",\n\t");
    int colIndex = 0;
    for(Column c: t.getNonPrimaryColumns()) {
      paramList
        .append(inparamTmpl
          .replace("{columnname}", c.getName())
          .replace("{sqldatatype}", DataTypeUtil.resolveSqlType(c.getDataType())));
      setList
        .append(setValueTmpl
          .replace("{columnname}", c.getName()));
      if(colIndex++ < t.getNonPrimaryColumns().size() - 1) {
        paramList.append(",\n\t");
        setList.append(", ");
      }
    }
    StringBuilder b = new StringBuilder();
    b
    .append(updateTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{paramlist}", paramList.toString())
      .replace("{tablename}", t.getName())
      .replace("{setlist}", setList.toString())
      .replace("{primarykey}", t.getPrimaryColumn().getName()));
    procedures.add(String.format("sp_update%s", t.getPascalName()));
    return b.toString();
  }

  private String generateInsert(Table t) {
    StringBuilder paramList = new StringBuilder();
    StringBuilder colNames = new StringBuilder();
    int colIndex = 0;
    for(Column c: t.getNonPrimaryColumns()) {
      paramList
        .append(inparamTmpl
          .replace("{columnname}", c.getName())
          .replace("{sqldatatype}", DataTypeUtil.resolveSqlType(c.getDataType())));
      colNames.append(c.getName());
      if(colIndex++ < t.getNonPrimaryColumns().size() - 1) {
        paramList.append(", ");
        colNames.append(", ");
      }
    }
    StringBuilder b = new StringBuilder();
    b
    .append(insertTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{paramlist}", paramList.toString())
      .replace("{tablename}", t.getName())
      .replace("{colnames}", colNames.toString())
      .replace("{primarykey}", t.getPrimaryColumn().getName()));
    procedures.add(String.format("sp_insert%s", t.getPascalName()));
    return b.toString();
  }

  private String generateDeleteByPk(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(deleteByPkTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{primarykey}", t.getPrimaryColumn().getName())
      .replace("{tablename}", t.getName()));
    procedures.add(String.format("sp_delete%s", t.getPascalName()));
    return b.toString();
  }

  private String generateDeleteJoined(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(deleteJoinedTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{tablename}", t.getName())
      .replace("{pk1}", t.getColumns().get(0).getName())
      .replace("{pk2}", t.getColumns().get(1).getName()));
    procedures.add(String.format("sp_delete%s", t.getPascalName()));
    return b.toString();
  }

  private String generateGrants() {
    StringBuilder b = new StringBuilder();
    for(String p : procedures) {
      b.append(grantExecTmpl
        .replace("{procedure}", p)
        .replace("{dbuser}", db.getUser()));
    }
    return b.toString();
  } 

  private void loadTemplates() {
    createTableTmpl = loadTemplate("../templates/sql", "createtable");
    deleteJoinedTmpl = loadTemplate("../templates/sql", "deletejoined");
    deleteByPkTmpl = loadTemplate("../templates/sql", "deletebypk");
    selectAllTmpl = loadTemplate("../templates/sql", "selectall");
    selectByPkTmpl = loadTemplate("../templates/sql", "selectbypk");
    selectByUniqueTmpl = loadTemplate("../templates/sql", "selectbyunique");
    selectLoopedTmpl = loadTemplate("../templates/sql", "selectoflooped");
    selectJoinedTmpl = loadTemplate("../templates/sql", "selectofjoined");
    selectOfParentTmpl = loadTemplate("../templates/sql", "selectofparent");
    insertTmpl = loadTemplate("../templates/sql", "insert");
    updateTmpl = loadTemplate("../templates/sql", "update");
    inparamTmpl = loadTemplate("../templates/sql", "inparam");
    fieldTmpl = loadTemplate("../templates/sql", "field");
    primaryKeyTmpl = loadTemplate("../templates/sql", "primarykey");
    setValueTmpl = loadTemplate("../templates/sql", "setvalue");
    uniqueFieldTmpl = loadTemplate("../templates/sql", "uniquefield");
    foreignKeyTmpl = loadTemplate("../templates/sql", "foreignkey");
    grantExecTmpl = loadTemplate("../templates/sql", "grantexec");
  }
}