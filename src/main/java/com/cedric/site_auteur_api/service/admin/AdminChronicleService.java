package com.cedric.site_auteur_api.service.admin;

import org.springframework.stereotype.Service;

import com.cedric.site_auteur_api.dto.chronicle.ChronicleCreateDto;
import com.cedric.site_auteur_api.dto.chronicle.AdminChronicleDto;
import com.cedric.site_auteur_api.dto.chronicle.ChronicleUpdateDto;
import com.cedric.site_auteur_api.entity.Chronicle;
import com.cedric.site_auteur_api.mapper.ChronicleMapper;
import com.cedric.site_auteur_api.repository.ChronicleRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AdminChronicleService {
    
    private final ChronicleRepository chronicleRepository;

    public AdminChronicleService(ChronicleRepository chronicleRepository) {
        this.chronicleRepository = chronicleRepository;
    }

    // all
    public List<AdminChronicleDto>getAllChronicles() {
        //findAll() récupère toutes les entités
        //.stream() parcourt la liste
        //.map(ChronicleMapper::toDto) convertit chaque entité en DTO
        //.toList() renvoie une liste de DTO
        return chronicleRepository.findAll()
            .stream()
            .map(ChronicleMapper::toAdminDto)
            .toList();

    }

    // By id
    public AdminChronicleDto getChronicleById(Integer id) {
        Chronicle chronicle = chronicleRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Chronique non trouvée avec l'id : " + id));

        return ChronicleMapper.toAdminDto(chronicle);
    }

    //Create
    public AdminChronicleDto createChronicle(ChronicleCreateDto dto) {
        // creer une nouvelle entité
        Chronicle chronicle = new Chronicle();

        chronicle.setTitle(dto.title());
        chronicle.setQuote(dto.quote());
        chronicle.setSummary(dto.summary());
        chronicle.setContent(dto.content());
        chronicle.setCoverUrl(dto.coverUrl());
        chronicle.setPublishedAt(dto.publishedAt());
        chronicle.setIsActive(dto.isActive() != null ? dto.isActive() : true);

        chronicle.setCreatedAt(OffsetDateTime.now());
        chronicle.setUpdatedAt(OffsetDateTime.now());

        Chronicle saved = chronicleRepository.save(chronicle);

        return ChronicleMapper.toAdminDto(saved);
    }

    //Update
    public AdminChronicleDto updateChronicle(Integer id, ChronicleUpdateDto dto) {

        // find by id
        Chronicle chronicle = chronicleRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Aucune chronique trouvée avec l'id : " + id));
        
        // Mise a jour des dto
        chronicle.setTitle(dto.title());
        chronicle.setQuote(dto.quote());
        chronicle.setSummary(dto.summary());
        chronicle.setContent(dto.content());
        chronicle.setCoverUrl(dto.coverUrl());
        chronicle.setIsActive(dto.isActive());
        chronicle.setPublishedAt(dto.publishedAt());
        chronicle.setUpdatedAt(OffsetDateTime.now());

        //retour de la chronicle mise a jour
        Chronicle updated = chronicleRepository.save(chronicle);

        return ChronicleMapper.toAdminDto(updated);
    }

    //Toggle is_active
    public AdminChronicleDto toggleChronicle(Integer id) {
        Chronicle chronicle = chronicleRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Aucune chronique trouvée avec l'id : " + id));

        chronicle.setIsActive(!chronicle.getIsActive());
        chronicle.setUpdatedAt(OffsetDateTime.now());

        Chronicle updated = chronicleRepository.save(chronicle);

        return ChronicleMapper.toAdminDto(updated);
    }

    // DELETE 
    public void deleteChronicle(Integer id) {
        if (!chronicleRepository.existsById(id)) {
            throw new NoSuchElementException("Chronique non trouvée avec l'id : " + id);
        }
        chronicleRepository.deleteById(id);
    }
}
