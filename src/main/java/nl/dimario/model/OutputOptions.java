package nl.dimario.model;

public class OutputOptions {

    private boolean addDefinitionsConfig;
    private boolean addQuotesToPlaceholder;
    private boolean removeQuotesFromArray;
    private boolean removeExtraQuotes;

    public boolean isAddDefinitionsConfig() {
        return addDefinitionsConfig;
    }

    public void setAddDefinitionsConfig(boolean addDefinitionsConfig) {
        this.addDefinitionsConfig = addDefinitionsConfig;
    }

    public boolean isAddQuotesToPlaceholder() {
        return addQuotesToPlaceholder;
    }

    public void setAddQuotesToPlaceholder(boolean addQuotesToPlaceholder) {
        this.addQuotesToPlaceholder = addQuotesToPlaceholder;
    }

    public boolean isRemoveQuotesFromArray() {
        return removeQuotesFromArray;
    }

    public void setRemoveQuotesFromArray(boolean removeQuotesFromArray) {
        this.removeQuotesFromArray = removeQuotesFromArray;
    }

    public boolean isRemoveExtraQuotes() {
        return removeExtraQuotes;
    }

    public void setRemoveExtraQuotes(boolean removeExtraQuotes) {
        this.removeExtraQuotes = removeExtraQuotes;
    }
}
