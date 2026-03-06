package kfu.itis.maslennikov.repository;

import kfu.itis.maslennikov.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UserRepository {

    private final SessionFactory sessionFactory;

    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(readOnly = true)
    public List<User> findAll(){
        Session session;
        try{
            session = sessionFactory.getCurrentSession();
        } catch (HibernateException e){
            session = sessionFactory.openSession();
        }

        return session.createQuery("from User").list();
    }

}
