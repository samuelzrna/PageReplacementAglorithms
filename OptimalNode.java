public class OptimalNode implements Comparable<OptimalNode>{

    private int page;
    private Integer nextIndex;

    public OptimalNode(int page, Integer nextIndex){
        this.page = page;
        this.nextIndex = nextIndex;
    }

    public Integer getNextIndex() {
        return nextIndex;
    }

    public int getPage() {
        return page;
    }

    public void setNextIndex(Integer nextIndex) {
        this.nextIndex = nextIndex;
    }

    @Override
    public int compareTo(OptimalNode o) {
        return this.getNextIndex().compareTo(o.getNextIndex());
    }

    public String toString(){
        return "page: " +  getPage() +  "\tnext index: " + getNextIndex();
    }
}
