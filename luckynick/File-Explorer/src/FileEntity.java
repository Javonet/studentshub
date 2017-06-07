import com.javonet.JavonetException;
import com.javonet.api.NObject;

import java.util.Date;

public class FileEntity {

    public String name, path;
    public char type;
    public double size;
    public Date modified;

    protected FileEntity() {

        type = 'd';
        size = 0;
    }
}
