package server.es;

import java.util.List;

public class GridData<T> {

    private List<T> data;

    private int total;

    public GridData() {
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
