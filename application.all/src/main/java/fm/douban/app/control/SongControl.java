package fm.douban.app.control;

import fm.douban.model.Song;
import fm.douban.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SongControl {

    @Autowired
    private SongService songService;

    @GetMapping(path = "/song/random")
    @ResponseBody
    public Song randomSong() {
        return songService.getRandomSong(1).get(0);
    }
}
