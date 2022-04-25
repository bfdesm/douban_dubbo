package java.fm.douban.spider;

import com.alibaba.fastjson.JSON;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.model.User;
import fm.douban.service.SubjectService;
import fm.douban.util.HttpUtil;
import fm.douban.util.IsNullUtil;
import fm.douban.util.SpideredUtil;
import fm.douban.util.SubjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SubjectSpider {
    private static final Logger LOG = LoggerFactory.getLogger(SubjectSpider.class);

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private SpideredUtil spideredUtil;

    private static final String MHZ_REFERER = "https://fm.douban.com/";
    private static final String HOST = "fm.douban.com";
    private static final String COOKIE =
            "viewed=\"26259017\"; bid=Ixdu8ZtzEXw; gr_user_id=68d4e16d-24f6-45a1-bad5-e354cbb2dd56; __utma=30149280.1510717963.1638194508.1638194508.1638194508.1; __utmz=30149280.1638194508.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _pk_ref.100001.f71f=%5B%22%22%2C%22%22%2C1642772724%2C%22https%3A%2F%2Fwww.baidu.com%2Fs%3Fie%3Dutf-8%26f%3D8%26rsv_bp%3D1%26ch%3D%26tn%3Dbaidu%26bar%3D%26wd%3D%25E8%25B1%2586%25E7%2593%25A3FM%26oq%3D%2525E8%2525B1%252586%2525E7%252593%2525A3%26rsv_pq%3Df6837070000030a9%26rsv_t%3D120bl0XlmRxmiiPJ5LNEasCYTUA1uuAvMsQlYVXaFCFAgnog74w3NrEvAZc%26rqlang%3Dcn%26rsv_enter%3D1%26rsv_dl%3Dtb%26rsv_sug3%3D3%26rsv_sug1%3D3%26rsv_sug7%3D100%22%5D; _ga=GA1.2.1510717963.1638194508; _gid=GA1.2.82405320.1642772725; _pk_id.100001.f71f=97dee38d0a3f8eaf.1642772724.1.1642773049.1642772724.";
    private static final String SL_REFERER = "https://fm.douban.com/explore/songlists";

    private static final String HOT_MHZ_URL = "https://fm.douban.com/j/v2/rec_channels?specific=all";

    private static final String HOT_COLLECTION_URL =
            "https://fm.douban.com/j/v2/songlist/explore?type=hot&genre=0&limit=20&sample_cnt=5";

    private static final String MHZ_URL = "https://fm.douban.com/j/v2/channel_info?id={0}";

    private static final String CHANNEL_URL = "https://fm.douban.com/j/v2/channel_info?id={0}";

    private static final String SINGER_URL = "https://fm.douban.com/j/v2/artist/{0}/";

    private static final String PLAY_SUBJECT_SONGS_URL =
            "https://fm.douban.com/j/v2/playlist?channel={0}&kbps=128&client=s%3Amainsite%7Cy%3A3.0&app_name=radio_website&version=100&type=n";

    private static final String COLLECTION_URL = "https://fm.douban.com/j/v2/songlist/{0}/";

    @PostConstruct
    public void init(){
        LOG.info(this.getClass()+" is built successfully");
    }

    private void getPlaySubjectSongsData(Subject subject) {
        String subjectId = subject.getId();
        String songDataUrl = MessageFormat.format(PLAY_SUBJECT_SONGS_URL, subjectId);

        Map<String, String> headerData = httpUtil.buildHeaderData(MHZ_REFERER, HOST, COOKIE);
        String content = httpUtil.getContent(songDataUrl, headerData);

        if (!StringUtils.hasText(content)) {
            return;
        }

        Map dataObj = null;

        try {
            dataObj = JSON.parseObject(content, Map.class);
        } catch (Exception e) {
            // 抛异常表示返回的内容不正确，不是正常的 json 格式，可能是网络或服务器出错了。
            LOG.error("parse content to map error. ");
        }

        // 可能格式错误
        if (dataObj == null) {
            return;
        }

        List<Map> songsData = (List<Map>) dataObj.get("song");

        if (songsData == null || songsData.isEmpty()) {
            return;
        }
        List<String> songIdList = new ArrayList<>();
        for (Map songData : songsData) {
            Song song = new Song();
            spideredUtil.buildSong(songData, song);
            spideredUtil.saveSong(song);
            songIdList.add(song.getId());
        }

        // 有元素则进行修改
        if (!songIdList.isEmpty()) {
            subject.setSongIds(songIdList);
        }
    }

    private void addMHZSubject(List<Map> channels, String subjectSubType) {
        if (IsNullUtil.isNull(channels))
            return;
        for (Map channelObj : channels) {
            Subject subject = new Subject();
            spideredUtil.buildSubject(channelObj, subject);
            subject.setSubjectType(SubjectUtil.TYPE_MHZ);
            subject.setSubjectSubType(subjectSubType);
            if (SubjectUtil.TYPE_SUB_ARTIST.equals(subjectSubType)) {
                // 记录关联的歌手
                List relatedArtists = (List) channelObj.get("related_artists");
                spideredUtil.addSingers(relatedArtists);
            }
            // 保存MHZ数据
            spideredUtil.saveSubject(subject);
            getPlaySubjectSongsData(subject);
        }
    }

    public Subject getCollectionByColletionId(String collectionId, Subject collection) {
        String colletionUrl = MessageFormat.format(COLLECTION_URL, collectionId);
        Map<String, String> headerData = httpUtil.buildHeaderData(SL_REFERER, HOST, COOKIE);
        String content = httpUtil.getContent(colletionUrl, headerData);
        if (!StringUtils.hasText(content)) {
            return collection;
        }
        Map dataMap = null;
        try {
            dataMap = JSON.parseObject(content, Map.class);
        } catch (Exception e) {
            // 抛异常表示返回的内容不正确，不是正常的 json 格式，可能是网络或服务器出错了。
            LOG.error("parse content to map error. ", e);
        }
        if (IsNullUtil.isNull(dataMap))
            return collection;
        User user = new User();
        spideredUtil.buildCreator(dataMap,user);
        spideredUtil.buildSubject(dataMap,collection);
        collection.setSubjectType(SubjectUtil.TYPE_COLLECTION);
        return collection;
    }

    public Subject getMHzArtistBysingerId(String singerId, Subject subject) {
        String url = MessageFormat.format(SINGER_URL, singerId);
        Map<String, String> headerData = httpUtil.buildHeaderData(MHZ_REFERER, HOST, COOKIE);
        String content = httpUtil.getContent(url, headerData);
        if (!StringUtils.hasText(content)) {
            return subject;
        }

        Map dataObj = null;

        try {
            dataObj = JSON.parseObject(content, Map.class);
        } catch (Exception e) {
            // 抛异常表示返回的内容不正确，不是正常的 json 格式，可能是网络或服务器出错了。
            LOG.error("parse content to map error. ", e);
        }

        // 可能格式错误
        if (spideredUtil.sourceDataIsNull(dataObj))
            return subject;

        Map related_channel_Data = (Map) dataObj.get("related_channel");
        Map songlist_Data = (Map) dataObj.get("songlist");
        spideredUtil.getSongListSongs(songlist_Data, new ArrayList<>());
        subject.setSubjectType(SubjectUtil.TYPE_MHZ);
        subject.setSubjectSubType(SubjectUtil.TYPE_SUB_ARTIST);
        spideredUtil.getRelatedChannel(related_channel_Data, subject);
        return subject;
    }

    public Subject getMHzBySubjectId(String subjectId, Subject subject) {
        return getMhzByUrl(MessageFormat.format(MHZ_URL, subjectId), subject);
    }

    public Subject getMhzByUrl(String url, Subject subject) {
        Map<String, String> headerData = httpUtil.buildHeaderData(MHZ_REFERER, HOST, COOKIE);
        String content = httpUtil.getContent(url, headerData);
        if (!StringUtils.hasText(content)) {
            return subject;
        }

        Map dataObj = null;

        try {
            dataObj = JSON.parseObject(content, Map.class);
        } catch (Exception e) {
            // 抛异常表示返回的内容不正确，不是正常的 json 格式，可能是网络或服务器出错了。
            LOG.error("parse content to map error. ", e);
        }

        // 可能格式错误
        if (spideredUtil.sourceDataIsNull(dataObj))
            return subject;

        Map related_channel_Data = (Map) dataObj.get("related_channel");
        Map songlist_Data = (Map) dataObj.get("songlist");
        spideredUtil.getSongListSongs(songlist_Data, new ArrayList<>());
        spideredUtil.getRelatedChannel(related_channel_Data, subject);
        return subject;
    }
}
