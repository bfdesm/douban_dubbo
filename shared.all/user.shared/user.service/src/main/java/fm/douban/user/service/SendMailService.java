package fm.douban.user.service;

public interface SendMailService {

    Boolean sendMail(String mail, String code);
}
