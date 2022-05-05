package fm.douban.app.control;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fm.douban.dao.CommentDAO;
import fm.douban.dataobject.CommentDO;
import fm.douban.model.Comment;
import fm.douban.model.Paging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentDAO commentDAO;

    @GetMapping("/comments")
    @ResponseBody
    public Paging<CommentDO> getAll(@RequestParam(value = "pageNum", required = false) Integer pageNum,
                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 15;
        }

        // 设置当前页数为1，以及每页3条记录
        Page<CommentDO> page = PageHelper.startPage(pageNum, pageSize).doSelectPage(() -> commentDAO.findAll());

        return new Paging<>(page.getPageNum(), page.getPageSize(), page.getPages(), page.getTotal(), page.getResult());
    }

    @PostMapping("/comment")
    @ResponseBody
    public CommentDO save(@RequestBody CommentDO commentDO) {
        commentDAO.insert(commentDO);
        return commentDO;
    }

    @PostMapping("/comment/batchAdd")
    @ResponseBody
    public List<CommentDO> batchAdd(@RequestBody List<CommentDO> commentDOS) {
        commentDAO.batchAdd(commentDOS);
        return commentDOS;
    }

    @PostMapping("/comment/update")
    @ResponseBody
    public CommentDO update(@RequestBody CommentDO commentDO) {
        commentDAO.update(commentDO);
        return commentDO;
    }

    @GetMapping("/comment/del")
    @ResponseBody
    public boolean delete(@RequestParam("id") Long id) {
        return commentDAO.delete(id) > 0;
    }

    @GetMapping("/comment/findByRefId")
    @ResponseBody
    public List<Comment> findByRefId(@RequestParam("refId") String refId) {
        return commentDAO.findByRefId(refId);
    }

    @GetMapping("/comment/findByUserIds")
    @ResponseBody
    public List<CommentDO> findByUserIds(@RequestParam("userIds") List<Long> ids) {
        return commentDAO.findByUserIds(ids);
    }
}
