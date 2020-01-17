

public class AxiosGenerator {
    private Database db;

    public AxiosGenerator(Database db) {
        this.db = db;
    }

    public String generateActions() {
        StringBuilder b = new StringBuilder();
        b.append("import axios from 'axios';\n\n");
        for(Table t: db.getTables()) {
            b
            .append(generateGetAll(t))
            .append("\n")
            .append(generateGetByPk(t))
            .append("\n")
            .append(generatGetByUnique(t))
            .append("\n")
            .append(generatePost(t))
            .append("\n")
            .append(generatePut(t))
            .append("\n")
            .append(generateDelete(t))
            .append("\n");
        }
        return b.toString();
    }

    private String generateGetAll(Table t) {
        StringBuilder b = new StringBuilder();
        b
        .append(generateExportCode(null,
            String.format("getAll%ss", t.getCleanName()), 
            String.format("%ss", t.getLowerCasedName())))
        .append(getAxiosReturnString("get", null))
        .append("};\n");
        return b.toString();
    }

    private String generateGetByPk(Table t) {
        StringBuilder b = new StringBuilder();
        b
        .append(generateExportCode(t.getPrimaryColumn().getPascalName(),
            String.format("get%sByPk", t.getCleanName()), 
            String.format("%ss/${%s}", t.getLowerCasedName(), t.getPrimaryColumn().getPascalName())))
        .append(getAxiosReturnString("get", null))
        .append("};\n");
        return b.toString();
    }

    private String generatGetByUnique(Table t) {
        StringBuilder b = new StringBuilder();
        for(Column col: t.getUniqueCols()) {
            b
            .append(generateExportCode(col.getPascalName(),
                String.format("get%sBy%s", t.getCleanName(), col.getCleanName()), 
                String.format("%ss/${%s}", t.getLowerCasedName(), col.getPascalName())))
            .append(getAxiosReturnString("get", null))
            .append("};\n");
        }
        return b.toString();
    }

    private String generatePost(Table t) {
        StringBuilder b = new StringBuilder();
        b
        .append(generateExportCode(t.getLowerCasedName(),
            String.format("post%s", t.getCleanName()), 
            String.format("%ss", t.getLowerCasedName())))
        .append(getAxiosReturnString("post", t.getLowerCasedName()))
        .append("};\n");
        return b.toString();
    }

    private String generatePut(Table t) {
        StringBuilder b = new StringBuilder();
        b
        .append(generateExportCode(t.getLowerCasedName(),
            String.format("put%s", t.getCleanName()), 
            String.format("%ss", t.getLowerCasedName())))
        .append(getAxiosReturnString("put", t.getLowerCasedName()))
        .append("};\n");
        return b.toString();
    }

    private String generateDelete(Table t) {
        StringBuilder b = new StringBuilder();
        b
        .append(generateExportCode(t.getPrimaryColumn().getPascalName(),
            String.format("delete%s", t.getCleanName()), 
            String.format("%ss/${%s}", t.getLowerCasedName(), t.getPrimaryColumn().getPascalName())))
        .append(getAxiosReturnString("delete", null))
        .append("};\n");
        return b.toString();
    }

    private String generateExportCode(String parameter, String functionName, String url) {
        StringBuilder b = new StringBuilder();
        b
        .append("export function ")
        .append(functionName)
        .append("(");
        if (parameter!= null) {
            b
            .append(parameter)
            .append(", ");
        }
        b
        .append("onSuccess, onError) {\n")
        .append("\tconst url = `/api/")
        .append(url)
        .append("`;\n");
        return b.toString();
    }

    private String getAxiosReturnString(String method, String parameter) {
        StringBuilder b = new StringBuilder();
        b
        .append("\treturn axios.")
        .append(method)
        .append("(url");
        if (parameter != null) {
            b
            .append(", ")
            .append(parameter);
        }
        b
        .append(")\n")
        .append("\t\t.then(result => {\n")
        .append("\t\t\tonSuccess(result);\n")
        .append("\t\t})\n")
        .append("\t\t.catch(err => {\n")
        .append("\t\t\tonError(err);\n")
        .append("\t\t});\n");
        return b.toString();
    }
}
