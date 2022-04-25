package fm.douban.app.control;

import fm.douban.model.Song;
import fm.douban.service.SongService;
import fm.douban.service.impl.SongServiceImpl;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

@Controller
public class SongControl {

    @DubboReference(version = "${song.service.version}")
    private SongService songService;

    private static final Logger LOG = LoggerFactory.getLogger(SongControl.class);

    @PostConstruct
    public void init(){
        LOG.info(this.getClass()+" is built successfully");
    }

    @GetMapping(path = "/song/random")
    @ResponseBody
    public Song randomSong() {
        return songService.getRandomSong(1).get(0);
    }
}
