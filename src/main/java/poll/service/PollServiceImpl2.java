//// Implementation of the business logic, living in the service sub-package.
//// Discoverable for auto-configuration, thanks to the @Component annotation.
//
//package poll.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import poll.model.Poll;
//import poll.repository.WelcomeRepository;
//
//import java.util.*;
//
//@Component
//public class PollServiceImpl2 implements PollService {
//
//    // Very simple in-memory database; key is the lang field of class Poll.
//    // We have to be careful with this 'database'. In order to avoid objects
//    // in the database being mutated accidentally, we must always copy objects
//    // before insertion and retrieval.
//    @Autowired
//    private WelcomeRepository db;
//
//    public PollServiceImpl2(WelcomeRepository repository) {
//        db = repository;
//    }
//
//    // Adds a poll to the database, or overwrites an existing one.
//    public void addWelcome(Poll poll) {
//        if (poll != null && poll.getLang() != null) {
//            db.save(poll);
//        }
//    }
//
//    // Returns a poll in language lang, personalised to name.
//    // Parameter lang must not be null, but name may be null.
//    public Poll getWelcome(String lang, String name) {
//        Poll poll = db.getWelcomeByLang(lang);
//        if (poll == null) {
//            return null;
//        }
//        if (name != null) {
//            poll.setMsg(poll.getMsg() + ", " + name);
//        }
//        return poll;
//    }
//
//    public List<Poll> getAllWelcomes() {
//        return new ArrayList<>((Collection) db.findAll());
//    }
//
//    public void removeWelcome(String lang) {
//        if (lang != null) {
//            db.deleteById(lang);
//        }
//    }
//
//}
