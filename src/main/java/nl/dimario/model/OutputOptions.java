package nl.dimario.model;

public class OutputOptions {

    private boolean addDefinitionsConfig;
    private boolean removeUuids;


    public boolean isAddDefinitionsConfig() {
        return addDefinitionsConfig;
    }

    public void setAddDefinitionsConfig(boolean addDefinitionsConfig) {
        this.addDefinitionsConfig = addDefinitionsConfig;
    }

    public boolean isRemoveUuids() {
        return removeUuids;
    }

    public void setRemoveUuids(boolean removeUuids) {
        this.removeUuids = removeUuids;
    }
}
