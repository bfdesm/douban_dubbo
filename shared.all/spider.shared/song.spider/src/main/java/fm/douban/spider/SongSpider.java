package fm.douban.spider;

import fm.douban.model.Song;
import fm.douban.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

@Component
public class SongSpider {
    private static final Logger LOG = LoggerFactory.getLogger(SongSpider.class);

    private static final String HOST = "fm.douban.com";
    private static final String REFERER = "https://fm.douban.com/";
    private static final String COOKIE = "viewed=\"26259017\"; bid=Ixdu8ZtzEXw; gr_user_id=68d4e16d-24f6-45a1-bad5-e354cbb2dd56; __utma=30149280.1510717963.1638194508.1638194508.1638194508.1; __utmz=30149280.1638194508.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _pk_ref.100001.f71f=%5B%22%22%2C%22%22%2C1642772724%2C%22https%3A%2F%2Fwww.baidu.com%2Fs%3Fie%3Dutf-8%26f%3D8%26rsv_bp%3D1%26ch%3D%26tn%3Dbaidu%26bar%3D%26wd%3D%25E8%25B1%2586%25E7%2593%25A3FM%26oq%3D%2525E8%2525B1%252586%2525E7%252593%2525A3%26rsv_pq%3Df6837070000030a9%26rsv_t%3D120bl0XlmRxmiiPJ5LNEasCYTUA1uuAvMsQlYVXaFCFAgnog74w3NrEvAZc%26rqlang%3Dcn%26rsv_enter%3D1%26rsv_dl%3Dtb%26rsv_sug3%3D3%26rsv_sug1%3D3%26rsv_sug7%3D100%22%5D; _ga=GA1.2.1510717963.1638194508; _gid=GA1.2.82405320.1642772725; _pk_id.100001.f71f=97dee38d0a3f8eaf.1642772724.1.1642773049.1642772724.";

    private static final String SINGER_URL = "https://fm.douban.com/j/v2/artist/{0}/";

    @Autowired
    private HttpUtil httpUtil;

    @PostConstruct
    public void init(){
        LOG.info(this.getClass()+" is built successfully");
    }


    public Song getSongBySongId(String songId,Song song){
        return null;
    }

    public void spiderSongImg(String url) {
        Map<String, String> headerData = httpUtil.buildHeaderData(REFERER, HOST, COOKIE);
        byte[] content = httpUtil.getBytes(url, headerData);
        LOG.error("!!!!!!!!!!!!!!!");
        try {
            Random random = new Random();
            File file = new File("D:"+ File.separator + "hello" + File.separator + Math.abs(random.nextInt()) +".jpg");  //1.指定要操作文件的路径
            if(!file.getParentFile().exists()){   //文件不存在
                file.getParentFile().mkdirs();   //创建父目录
            }
            FileOutputStream fos = new FileOutputStream(file);
            LOG.error("!!!!!!!!!!!!!!!");
            fos.write(content);
            //记得及时释放资源
            fos.close();
        } catch (IOException e) {
            LOG.error("error");
            LOG.error(String.valueOf(e));
        }
    }
}
