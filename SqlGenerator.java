
public class SqlGenerator extends Generator {
  private Database db;

  private String createTableTmpl;

  private String deleteByPkTmpl;
  private String selectAllTmpl;
  private String selectByPkTmpl;
  private String selectByUniqueTmpl;
  private String insertTmpl;
  private String updateTmpl;

  private String inparamTmpl;
  private String fieldTmpl;
  private String primaryKeyTmpl;
  private String setValueTmpl;
  private String uniqueFieldTmpl;
  private String foreignKeyTmpl;

  public SqlGenerator(Database db) {
    this.db = db;
    loadTemplates();
  }

  public String generateSql() {
    StringBuilder b = new StringBuilder();
    for(Table t: db.getTables()) {
      currentLoopedOrJoined = t.getIsJoined() || t.getIsLooped();
      b
      .append(generateCreateTable(t))
      .append(generateSelectAll(t));
      if(!currentLoopedOrJoined) {
        b
        .append(generateSelectByPk(t))
        .append(generatorSelectByUnique(t));
      }
      b.append("\n");
    }
    return b.toString();
  }

  private String generateSelectAll(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(selectAllTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{tablename}", t.getName())
      .replace("{tablename}", t.getName()));
    return b.toString();
  }

  private String generateSelectByPk(Table t) {
    StringBuilder b = new StringBuilder();
    b
    .append(selectByPkTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{tablename}", t.getName())
      .replace("{primarykey}", t.getPrimaryColumn().getName()));
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
    return b.toString();
  }

  private String generateInsert(Table t) {
    StringBuilder paramList = new StringBuilder();
    StringBuilder colNames = new StringBuilder();

    StringBuilder b = new StringBuilder();
    b
    .append(insertTmpl
      .replace("{tablenamepascal}", t.getPascalName())
      .replace("{paramlist}", paramList.toString())
      .replace("{tablename}", t.getName())
      .replace("{colnames}", colNames.toString())
      .replace("{primarykey}", t.getPrimaryColumn().getName()));
    return b.toString();
  }

  private void loadTemplates() {
    createTableTmpl = loadTemplate("createtable");
    deleteByPkTmpl = loadTemplate("deletebypk");
    selectAllTmpl = loadTemplate("selectall");
    selectByPkTmpl = loadTemplate("selectbypk");
    selectByUniqueTmpl = loadTemplate("selectbyunique");
    insertTmpl = loadTemplate("insert");
    updateTmpl = loadTemplate("update");
    inparamTmpl = loadTemplate("inparam");
    fieldTmpl = loadTemplate("field");
    primaryKeyTmpl = loadTemplate("primarykey");
    setValueTmpl = loadTemplate("setvalue");
    uniqueFieldTmpl = loadTemplate("uniquefield");
    foreignKeyTmpl = loadTemplate("foreignkey");
  }

  private String loadTemplate(String fileName) {
    FileHandler fh = new FileHandler(String.format("../templates/%s.tmpl", fileName));
    String content = fh.readFile();
    if(fh.isInError()) {
      System.out.println("Could not read " + fileName);
      return "";
    }
    return content;
  }
}