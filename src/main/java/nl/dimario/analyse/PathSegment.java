package nl.dimario.analyse;

public class PathSegment {

    private String nodeSegment;
    private String dirSegment;
    private int level;

    public PathSegment( int level, String nodeSegment, String dirSegment) {
        this.level = level;
        this.dirSegment = dirSegment;
        this.nodeSegment = nodeSegment;
    }

    public PathSegment( int level, String nodeSegment) {
        this.level = level;
        this.nodeSegment = nodeSegment;
        int pos = nodeSegment.indexOf(":");
        if( pos > -1) {
            this.dirSegment = nodeSegment.substring( pos+1);
        } else {
            this.dirSegment = nodeSegment;
        }
    }

    public String getDirSegment() {
        return dirSegment;
    }

    public void setDirSegment(String dirSegment) {
        this.dirSegment = dirSegment;
    }


    public String getNodeSegment() {
        return nodeSegment;
    }

    public void setNodeSegment(String nodeSegment) {
        this.nodeSegment = nodeSegment;
    }
}
