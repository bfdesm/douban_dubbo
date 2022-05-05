package fm.douban.service;


import fm.douban.model.Comment;
import fm.douban.model.Result;

import java.util.List;

public interface CommentService {

    /**
     * 发布评论
     *
     * @param refId    外部 ID
     * @param userId   用户 ID
     * @param parentId 父评论 ID
     * @param content  评论内容
     * @return
     */
    public Result<Comment> post(String refId, long userId, long parentId, String content);


    /**
     * 查询评论
     *
     * @param refId
     * @return
     */
    public Result<List<Comment>> query(String refId);
}
