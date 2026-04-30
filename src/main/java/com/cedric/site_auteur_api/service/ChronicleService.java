package com.cedric.site_auteur_api.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.cedric.site_auteur_api.mapper.ChronicleMapper;
import com.cedric.site_auteur_api.dto.PageResponse;

import com.cedric.site_auteur_api.dto.chronicle.ChronicleCreateDto;
import com.cedric.site_auteur_api.dto.chronicle.ChronicleDto;
import com.cedric.site_auteur_api.dto.chronicle.ChronicleUpdateDto;
import com.cedric.site_auteur_api.dto.chronicle.ChronicleListDto;
import com.cedric.site_auteur_api.entity.Chronicle;

import com.cedric.site_auteur_api.repository.ChronicleRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class ChronicleService {
    
    // Injection du repository
    private final ChronicleRepository chronicleRepository;

    // Constructeur
    //Ce constructeur permet à Spring d’injecter automatiquement le ChronicleRepository dans le service.
    //Grâce à l’injection par constructeur, le service reçoit sa dépendance au moment de sa création, 
    // ce qui garantit une classe immuable, testable et conforme aux bonnes pratiques Spring.
    public ChronicleService(ChronicleRepository chronicleRepository) {
        this.chronicleRepository = chronicleRepository;
    }

    // all
    public List<ChronicleDto>getAllChronicles() {
        //findAll() récupère toutes les entités
        //.stream() parcourt la liste
        //.map(ChronicleMapper::toDto) convertit chaque entité en DTO
        //.toList() renvoie une liste de DTO
        return chronicleRepository.findAll()
            .stream()
            .map(ChronicleMapper::toDto)
            .toList();
    
    }
    // all avec pagination 
    public PageResponse<ChronicleListDto> getChronicles(int page, int size){
        // 1 créer l'objet Pageable
        Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("createdAt").descending()
        );

        // 2 Appeler le repository pour récupérer une page d'entités Chronicle
        // Spring renvoie un objet Page<Chronicle> contenant :
        // - les entités de la page
        // - le total d'éléments
        // - le nombre total de pages
        // - les infos de navigation (hasNext, hasPrevious)
        Page<Chronicle> pageResult = chronicleRepository.findAll(pageable);

        // 3 Mapper les entités Chronicle => ChronicleListDto
        List<ChronicleListDto> items = pageResult.getContent()
            .stream()
            .map(ChronicleMapper::toListDto)
            .toList();

        // 4 Construire et retourner la réponse paginée
        // On remplit le DTO générique PageResponse<T> avec :
        // - les items mappés
        // - les infos de pagination fournies par Spring
        return new PageResponse<>(
            items,
            pageResult.getNumber(),
            pageResult.getSize(),
            pageResult.getTotalElements(),
            pageResult.getTotalPages(),
            pageResult.hasNext(),
            pageResult.hasPrevious()
        );
    }
    //By slug
    public ChronicleDto getChronicleBySlug(String slug) {
        Chronicle chronicle = chronicleRepository.findBySlug(slug);
        if (chronicle == null) {
            throw new NoSuchElementException("Livre non trouvé avec le slug : " + slug);
        }
        return ChronicleMapper.toDto(chronicle);
    }

    // By id
    public ChronicleDto getChronicleById(Integer id) {
        Chronicle chronicle = chronicleRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Chronique non trouvée avec l'id : " + id));

        return ChronicleMapper.toDto(chronicle);
    }

    // 3 Random
    public List<ChronicleDto> getRandom3Chronicles() {
        List<ChronicleDto> chronicles = chronicleRepository.findRandom3Chronicles()
            .stream()
            .map(ChronicleMapper::toDto)
            .toList();
        if( chronicles.isEmpty() ) {
            throw new NoSuchElementException("Aucune chronique trouvée");
        }
        return chronicles;
    }
    // 3 dernieres
    public List<ChronicleDto>get3LatestChronicles() {
        List<ChronicleDto> chronicles = chronicleRepository.findTop3ByOrderByPublishedAtDesc()
            .stream()
            .map(ChronicleMapper::toDto)
            .toList();
        if( chronicles.isEmpty() ) {
            throw new NoSuchElementException("Aucune chronique trouvée");
        }
        return chronicles;
    }

    //Delete
    public void deleteChronicleById(Integer id) {
        if (!chronicleRepository.existsById(id)) {
            throw new NoSuchElementException("Chronique non trouvée avec l'id : " + id);
        }
        chronicleRepository.deleteById(id);
    }

    //Update
    public ChronicleDto updateChronicle(Integer id, ChronicleUpdateDto data) {

        // find by id
        Chronicle chronicle = chronicleRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Aucune chronique trouvée avec l'id : " + id));
        
        // Mise a jour des data
        chronicle.setTitle(data.title());
        chronicle.setQuote(data.quote());
        chronicle.setSummary(data.summary());
        chronicle.setContent(data.content());
        chronicle.setCoverUrl(data.coverUrl());
        chronicle.setIsActive(data.isActive());
        chronicle.setPublishedAt(data.publishedAt());
        chronicle.setUpdatedAt(OffsetDateTime.now());

        //reotur chronicle mise a jour
        Chronicle updated = chronicleRepository.save(chronicle);

        return ChronicleMapper.toDto(updated);
    }

    //Toggle is_active
    public ChronicleDto toggleChronicle(Integer id) {
        Chronicle chronicle = chronicleRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Aucune chronique trouvée avec l'id : " + id));

        chronicle.setIsActive(!chronicle.getIsActive());
        chronicle.setUpdatedAt(OffsetDateTime.now());

        Chronicle updated = chronicleRepository.save(chronicle);

        return ChronicleMapper.toDto(updated);
    }

    //Create
    public ChronicleDto createChronicle(ChronicleCreateDto data) {
        // creer une nouvelle entité
        Chronicle chronicle = new Chronicle();

        chronicle.setTitle(data.title());
        chronicle.setQuote(data.quote());
        chronicle.setSummary(data.summary());
        chronicle.setContent(data.content());
        chronicle.setCoverUrl(data.coverUrl());
        chronicle.setPublishedAt(data.publishedAt());
        chronicle.setIsActive(data.isActive() != null ? data.isActive() : true);

        chronicle.setCreatedAt(OffsetDateTime.now());
        chronicle.setUpdatedAt(OffsetDateTime.now());

        Chronicle saved = chronicleRepository.save(chronicle);

        return ChronicleMapper.toDto(saved);
    }

    // DELETE
    public void deleteChronicle(Integer id) {
        if (!chronicleRepository.existsById(id)) {
            throw new NoSuchElementException("Chronique non trouvée avec l'id : " + id);
        }
        chronicleRepository.deleteById(id);
    }
    
}
