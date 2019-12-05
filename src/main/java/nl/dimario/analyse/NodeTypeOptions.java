package nl.dimario.analyse;

public class NodeTypeOptions {

    private String typeName;
    private int level;
    private boolean defCon;
    private boolean stopRecurse;

    public NodeTypeOptions( String typeName, int level) {
        this.level = level;
        this.typeName = typeName;
    }
    public String getTypeName() {
        return typeName;
    }

    public int getLevel() {
        return level;
    }

    public boolean isDefCon() {
        return defCon;
    }

    public void setDefCon(boolean defCon) {
        this.defCon = defCon;
    }

    public boolean isStopRecurse() {
        return stopRecurse;
    }

    public void setStopRecurse(boolean stopRecurse) {
        this.stopRecurse = stopRecurse;
    }
}
