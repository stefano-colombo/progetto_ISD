package dominio.messaggio;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MessaggioRepository extends CrudRepository<Messaggio, Long>{


}
