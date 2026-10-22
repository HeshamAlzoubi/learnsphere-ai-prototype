package org.learnsphere.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DemoAttemptRepository extends JpaRepository<DemoAttemptEntity,Long> {
 List<DemoAttemptEntity> findAllByOrderByIdDesc();
}
