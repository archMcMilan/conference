package ua.rd.cm.services.business_logic;

import java.util.List;

import ua.rd.cm.domain.Type;
import ua.rd.cm.dto.CreateTypeDto;
import ua.rd.cm.dto.TypeDto;

/**
 * @author Mariia Lapovska
 */
public interface TypeService {

    Type find(Long id);

    Long save(CreateTypeDto type);

    List<TypeDto> findAll();

    Type getByName(String name);
}
