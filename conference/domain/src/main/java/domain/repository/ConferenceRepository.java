package domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import domain.model.Conference;

@Repository
public interface ConferenceRepository extends CrudRepository<Conference, Long> {

    List<Conference> findAllByEndDateIsLessThan(LocalDate localDateTime);

    List<Conference> findAllByStartDateIsGreaterThanEqual(LocalDate localDateTime);

    @Override
    List<Conference> findAll();

    Conference findById(Long id);

}
