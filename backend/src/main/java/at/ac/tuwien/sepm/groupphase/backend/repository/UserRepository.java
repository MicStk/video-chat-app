package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {


    ApplicationUser findApplicationUserByEmail(String email);

    @Query("select u from ApplicationUser u where "
         + "(concat(upper(u.firstName), ' ', upper(u.lastName)) like concat(upper(?1), '%')) or "
         + "(upper(u.lastName) like concat(upper(?1), '%'))")
    List<ApplicationUser> findApplicationUserByName(String name);
}
