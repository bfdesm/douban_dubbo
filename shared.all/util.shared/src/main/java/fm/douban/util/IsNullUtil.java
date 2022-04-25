package fm.douban.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class IsNullUtil {

    private static final Logger LOG = LoggerFactory.getLogger(IsNullUtil.class);

    @PostConstruct
    public void init(){
        LOG.info(this.getClass()+" is built successfully");
    }

    public static boolean isNull(String a) {
        if (a == null)
            return true;
        return false;
    }

    public static boolean isNull(LocalDateTime a) {
        if (a == null)
            return true;
        return false;
    }

    public static boolean isNull(List a) {
        if (a == null)
            return true;
        return false;
    }

    public static boolean isNull(Integer a) {
        if (a == null)
            return true;
        return false;
    }

    public static boolean isNull(Map a) {
        if (a == null)
            return true;
        return false;
    }

    public static boolean isNull(LocalDate localDate){
        if(localDate == null)
            return true;
        return false;
    }

    public static boolean isNull(Long a){
        if(a == null)
            return true;
        return false;
    }
}
