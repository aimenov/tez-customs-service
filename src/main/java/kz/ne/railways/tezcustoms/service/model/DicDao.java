package kz.ne.railways.tezcustoms.service.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DicDao implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 
     */
    private long id;
    private String sid;
    private String name;
    private String exp;
    private HashMap<String, DicDao> data = new HashMap<String, DicDao>();
    private Map dinamycData = new HashMap();

    public DicDao() {}

    public DicDao(long _id, String _sid, String _name, String _exp) {
        this.id = _id;
        this.sid = _sid;
        this.name = _name;
        this.exp = _exp;
    }

    public DicDao(long _id, String _name) {
        this.id = _id;
        this.name = _name;
    }

    public DicDao(long _id, String _sid, String _name) {
        this.id = _id;
        this.sid = _sid;
        this.name = _name;
    }
}
