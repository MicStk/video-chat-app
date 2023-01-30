package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all message entries ordered by the timestamp date (descending).
     *
     * @return ordered list of all message entries
     */
    List<Message> findAllByOrderByTimestampDesc();

    /**
     * Find all message entries of a given user.
     *
     * @param user the user of the message entries
     *
     * @return a list of all message entries by a user
     */
    List<Message> findAllByUser(ApplicationUser user);

    @Query("select m from Message m where m.container.id = ?1")
    List<Message> findMessagesByContainer(Long containerId);

    @Query("select m from Message m join m.user u where "
         + "(?1 = null or upper(m.content) like concat('%', upper(?1), '%')) and "
         + "(?2 = null or concat(upper(u.firstName), ' ', upper(u.lastName)) like concat(upper(?2), '%') or upper(u.lastName) like concat(upper(?2), '%')) and "
         + "(?3 = null or m.timestamp >= ?3) and "
         + "(?4 = null or m.timestamp <= ?4) "
         + "order by m.timestamp desc")
    List<Message> searchMessagesByCriteria(String text, String author, LocalDateTime fromTime, LocalDateTime toTime);

}
