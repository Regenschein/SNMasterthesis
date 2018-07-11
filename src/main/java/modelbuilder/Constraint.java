package modelbuilder;

public class Constraint {

    private String attribute;
    private String constraint;
    private String value;

    public Constraint(String attribute, String constraint, String value){
        this.attribute = attribute;
        this.constraint = constraint;
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
    public String getConstraint() {
        return constraint;
    }
    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
