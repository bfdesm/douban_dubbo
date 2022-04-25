package fm.douban.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class FavoriteUtil {
    /**
     * 类型：红心
     */
    public static final String TYPE_RED_HEART = "redHeart";

    /**
     * 喜欢的元素的类型：歌曲
     */
    public static final String ITEM_TYPE_SONG = "song";

    /**
     * 喜欢的元素的类型：歌手
     */
    public static final String ITEM_TYPE_SINGER = "singer";

    /**
     * 喜欢的元素的类型：赫兹
     */
    public static final String ITEM_TYPE_MHZ = "mhz";

    /**
     * 喜欢的元素的类型：歌单
     */
    public static final String ITEM_TYPE_COLLECTION = "collection";

    private static final Logger LOG = LoggerFactory.getLogger(FavoriteUtil.class);

    @PostConstruct
    public void init(){
        LOG.info(this.getClass()+" is built successfully");
    }
}
