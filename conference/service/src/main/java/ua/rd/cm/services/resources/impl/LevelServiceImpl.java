package ua.rd.cm.services.resources.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ua.rd.cm.dto.LevelDto;
import ua.rd.cm.repository.LevelRepository;
import ua.rd.cm.services.resources.LevelService;

@Service
public class LevelServiceImpl implements LevelService {

    private ModelMapper modelMapper;
    private LevelRepository levelRepository;

    @Autowired
    public LevelServiceImpl(ModelMapper modelMapper, LevelRepository levelRepository) {
        this.modelMapper = modelMapper;
        this.levelRepository = levelRepository;
    }

    @Override
    public List<LevelDto> findAll() {
        return levelRepository.findAll().stream()
                .map(l -> modelMapper.map(l, LevelDto.class))
                .collect(Collectors.toList());
    }
}
