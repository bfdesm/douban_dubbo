package fm.douban.app.control;

import fm.douban.dataobject.CommentDO;
import fm.douban.model.PageView;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class PageViewConsumer {
    private static final Logger logger = LoggerFactory.getLogger(PageViewConsumer.class);

    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private RedisTemplate redisTemplate;

    @KafkaListener(topics = {"pageView"})
    public void mainPageViewlistener(ConsumerRecord<?, ?> record) {
        // #2. 如果消息存在
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            // #3. 获取消息
            PageView message = (PageView) kafkaMessage.get();
            Date date = message.getGmtCreated();
            String timeStr = buildTime(date);
            //获取分布式锁
            RLock transferLock = redisson.getLock("pageView-"+timeStr);
            transferLock.lock();
            try {
                increasePageView(message.getPageName() + "-pageView-" + timeStr, 1.0);
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                transferLock.unlock();
            }
        }
    }

    @KafkaListener(topics = {"commentView"})
    public void commentPageViewlistener(ConsumerRecord<?, ?> record) {
        // #2. 如果消息存在
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            // #3. 获取消息
            CommentDO message = (CommentDO) kafkaMessage.get();
            Date date = Date.from(Instant.from(message.getGmtCreated()));
            String timeStr = buildTime(date);
            //获取分布式锁
            RLock transferLock = redisson.getLock("commentView-"+timeStr);
            transferLock.lock();
            try {
                increasePageView( "commentView-" + message.getId()+"-"+ timeStr, 1.0);
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                transferLock.unlock();
            }
        }
    }

    @KafkaListener(topics = {"singerView"})
    public void singerPageViewlistener(ConsumerRecord<?, ?> record) {
        // #2. 如果消息存在
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            // #3. 获取消息
            Singer message = (Singer) kafkaMessage.get();
            Date date = Date.from(Instant.from(message.getGmtCreated()));
            String timeStr = buildTime(date);
            //获取分布式锁
            RLock transferLock = redisson.getLock("singerView-" + timeStr);
            transferLock.lock();
            try {
                increasePageView("singerView-"+ message.getId() + "-" + timeStr, 1.0);
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                transferLock.unlock();
            }
        }
    }

    @KafkaListener(topics = {"songView"})
    public void songPageViewlistener(ConsumerRecord<?, ?> record) {
        // #2. 如果消息存在
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            // #3. 获取消息
            Song message = (Song) kafkaMessage.get();
            Date date = Date.from(Instant.from(message.getGmtCreated()));
            String timeStr = buildTime(date);
            //获取分布式锁
            RLock transferLock = redisson.getLock("songView-"+timeStr);
            transferLock.lock();
            try {
                increasePageView( "songView-" + message.getId()+"-"+ timeStr, 1.0);
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                transferLock.unlock();
            }
        }
    }

    @KafkaListener(topics = {"subjectView"})
    public void subjectPageViewlistener(ConsumerRecord<?, ?> record) {
        // #2. 如果消息存在
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            // #3. 获取消息
            Subject message = (Subject) kafkaMessage.get();
            Date date = Date.from(Instant.from(message.getGmtCreated()));
            String timeStr = buildTime(date);
            //获取分布式锁
            RLock transferLock = redisson.getLock("subjectView-"+timeStr);
            transferLock.lock();
            try {
                increasePageView( "subjectView-" + message.getId()+"-"+ timeStr, 1.0);
            } catch (Exception e) {
                logger.error("", e);
            } finally {
                transferLock.unlock();
            }
        }
    }

    public void increasePageView(String key, Double i) {
        Double view = (Double)redisTemplate.opsForValue().get(key);
        if(view == null){
            redisTemplate.opsForValue().set(key, i);
        }else{
            redisTemplate.opsForValue().set(key, view + i);
        }
    }

    private String buildTime(Date date){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String timeStr = df.format(localDate);
        return timeStr;
    }
}
