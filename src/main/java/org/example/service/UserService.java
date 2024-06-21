package org.example.service;

import org.example.model.Student;
import org.example.model.User;
import org.example.validation.sessionValidation.SessionValidation;
import org.example.util.PasswordUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class UserService {

    private EntityManagerFactory entityManagerFactory;
    private SessionValidation sessionValidation;

    private User authenticatedUser; // Este es el usuario autenticado actual


    public UserService() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("default"); // Asegúrate de que el nombre coincide con el de tu persistence-unit
        this.sessionValidation = new SessionValidation();

    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public User findUserByUsernameAndPassword(String username, String password) {
        if (sessionValidation.isBlocked(username)) {
            long remainingLockTime = sessionValidation.getRemainingLockTime(username);
            System.out.println("Usuario temporalmente bloqueado. Intente nuevamente en " + remainingLockTime / 1000 + " segundos.");
            return null;
        }

        EntityManager em = entityManagerFactory.createEntityManager();
        User user = null;

        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            user = query.getSingleResult();

            if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
                sessionValidation.loginSucceeded(username);
            } else {
                sessionValidation.loginFailed(username);
                user = null;
            }
        } catch (Exception e) {
            sessionValidation.loginFailed(username);
            e.printStackTrace();
        } finally {
            em.close();
        }

        return user;
    }

    public boolean createUser(User user) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
            user.setPasswordChangedAt(new Date());
            em.persist(user);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean updatePassword(User user, String newPassword) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            user = em.merge(user);
            user.setPassword(PasswordUtil.hashPassword(newPassword));
            user.setPasswordChangedAt(new Date());
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public boolean saveStudents(User user, List<Object[]> rows) {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            em.getTransaction().begin();
            for (Object[] row : rows) {
                Student student = new Student();
                student.setName((String) row[0]);
                student.setSurname((String) row[1]);
                student.setSubjectName((String) row[2]);
                student.setGrade((Integer) row[3]);
                student.setUser(user);
                em.persist(student);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public List<Student> getStudentsByUser(User user) {
        EntityManager em = entityManagerFactory.createEntityManager();
        List<Student> students = null;
        try {
            students = em.createQuery("SELECT s FROM Student s WHERE s.user = :user", Student.class)
                    .setParameter("user", user)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return students;
    }

    public void loginSucceeded(String username) {
        sessionValidation.loginSucceeded(username);
    }

    public void loginFailed(String username) {
        sessionValidation.loginFailed(username);
    }

    public boolean isBlocked(String username) {
        return sessionValidation.isBlocked(username);
    }

    public long getRemainingLockTime(String username) {
        return sessionValidation.getRemainingLockTime(username);
    }

    public int getRemainingAttempts(String username) {
        return sessionValidation.getRemainingAttempts(username);
    }


    public void close() {
        entityManagerFactory.close();
    }
}
