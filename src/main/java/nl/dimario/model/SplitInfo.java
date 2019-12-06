package nl.dimario.model;

public class SplitInfo {

    private String nodeSegment;
    private String dirSegment;
    private String nodeType;

    public SplitInfo( String nodeSegment) {
        this.nodeSegment = nodeSegment;
        int pos = nodeSegment.lastIndexOf(":");
        if( pos > -1) {
            this.dirSegment = nodeSegment.substring( pos+1);
        } else {
            this.dirSegment = nodeSegment;
        }
    }

    public String getNodeSegment() {
        return nodeSegment;
    }

    public void setNodeSegment(String nodeSegment) {
        this.nodeSegment = nodeSegment;
    }

    public String getDirSegment() {
        return dirSegment;
    }

    public void setDirSegment(String dirSegment) {
        this.dirSegment = dirSegment;
    }
    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

}
