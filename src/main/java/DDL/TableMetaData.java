//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package DDL;

public class TableMetaData {
    private String column_name;
    private String data_type;
    private String size;
    private String constraint;

    public TableMetaData(String column_name, String data_type, String size, String constraint) {
        this.column_name = column_name;
        this.data_type = data_type;
        this.size = size;
        this.constraint = constraint;
    }

    public String getColumn_name() {
        return this.column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getData_type() {
        return this.data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getSize() {
        return this.size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getConstraint() {
        return this.constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String printMetaData() {
        return this.getColumn_name() + "|" + this.getData_type() + "|" + this.getSize() + "|" + this.getConstraint();
    }
}
